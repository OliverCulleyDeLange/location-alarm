import Shared
import Foundation
import Combine
import KMPNativeCoroutinesCombine

class AppViewModel: Shared.AppViewModel, Cancellable, LocationService.LocationServiceDelegate {
    private var locationService: LocationService = LocationService()
    private var alarmManager: AlarmManager = AlarmManager.shared
    private var activityManager: ActivityManager = ActivityManager.shared
    
    private var cancellables = Set<AnyCancellable>()
        
    @Published var alarmButtonText: String  = "Disable alarm"
    
    override init() {
        super.init()
        locationService.delegate = self
        
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
                    await alarmEnabled ? ActivityManager.shared.start(newDistanceToAlarm: self.state.distanceToGeofencePerimeter?.intValue, alarmTriggered: self.state.alarmTriggered) : ActivityManager.shared.stop()
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
                guard let distanceToAlarm = holder.distanceToGeofencePerimeter else {
                    logger.warning("Trying to update live location, but no available distance to alarm")
                    return
                }
                Task {
                    await ActivityManager.shared.updateActivity(
                        newDistanceToAlarm: distanceToAlarm,
                        alarmTriggered: holder.alarmTriggered
                    )
                }
            }
            .store(in: &cancellables)
        
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .map { $0.alarmTriggered }
            .removeDuplicates()
            .sink { alarmTriggered in
                if (alarmTriggered){
                    self.alarmManager.startAlarm()
                } else {
                    self.alarmManager.stopAlarm()
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
    
    func onLocationUpdate(locations: Array<Shared.Location>) {
        onLocationChange(locations: locations)
    }
    
    func onViewDidAppear() {
        locationService.checkLocationPermissionsAndStartListening()
    }
}

// TODO This is annoying to have to have, but i couldn't work out how to use a tuple with the publisher chain
private struct DistanceAndTriggered: Equatable {
    let distanceToGeofencePerimeter: Int?
    let alarmTriggered: Bool
}
