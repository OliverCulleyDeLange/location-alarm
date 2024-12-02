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
            .map { state in state.alarmEnabled}
            .removeDuplicates()
            .sink { alarmEnabled in
                self.alarmButtonText = alarmEnabled ? "Disable alarm" : "Enable Alarm"
            }
            .store(in: &cancellables)
    }
    
    func cancel() {
        cancellables = []
    }
}
