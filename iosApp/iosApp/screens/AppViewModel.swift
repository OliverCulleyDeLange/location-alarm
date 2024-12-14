import Shared
import Foundation
import Combine
import KMPNativeCoroutinesCombine

class AppViewModel: Shared.AppViewModel, Cancellable {

    private var cancellables = Set<AnyCancellable>()
    
    @Published var alarmButtonText: String  = "Disable alarm"

    override init() {
        super.init()
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .removeDuplicates()
            .sink { state in
                self.alarmButtonText = state.alarmEnabled ? "Disable alarm" : "Enable Alarm"
            }
            .store(in: &cancellables)
        
        // TODO DRY + This doesn't feel nice yet, but its better than having it in a .task in the view
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .map { state in state.alarmEnabled }
            .removeDuplicates()
            .sink { alarmEnabled in
                Task {
                    await alarmEnabled ? ActivityManager.shared.start() : ActivityManager.shared.stop()
                }
            }
            .store(in: &cancellables)
        
        // TODO DRY + This doesn't feel nice yet, but its better than having it in a .task in the view
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .filter { state in state.alarmEnabled}
            .map { DistanceAndTriggered(
                distanceToGeofencePerimeter: $0.distanceToGeofencePerimeter?.intValue,
                alarmTriggered: $0.alarmTriggered
            ) }
            .removeDuplicates()
            .sink { holder in
                guard let distanceToAlarm = holder.distanceToGeofencePerimeter else { return }
                Task {
                    await ActivityManager.shared.updateActivity(
                        newDistanceToAlarm: distanceToAlarm,
                        alarmTriggered: holder.alarmTriggered
                    )
                }
            }
            .store(in: &cancellables)
    }
    
    func cancel() {
        for c in cancellables {
            c.cancel()
        }
        cancellables = []
    }
}

// TODO This is annoying to have to have, but i couldn't work out how to use a tuple with the publisher chain
private struct DistanceAndTriggered: Equatable {
    let distanceToGeofencePerimeter: Int?
    let alarmTriggered: Bool
}
