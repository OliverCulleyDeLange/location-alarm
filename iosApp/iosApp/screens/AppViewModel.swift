import Shared
import UserNotifications
import Foundation
import Combine
import KMPNativeCoroutinesCombine

/// App side view model. Extends the shared kotlin view model and adds IOS specific functions.
class AppViewModel: Shared.AppViewModel, Cancellable, LocationService.LocationServiceDelegate {
    private var locationService: LocationService = LocationService()
    private var alarmManager: AlarmManager = AlarmManager.shared
    private var activityManager: LiveActivityManager = LiveActivityManager.shared
    
    private var cancellables = Set<AnyCancellable>()
    
    @Published var alarmButtonText: String  = "Enable alarm"
    
    override init() {
        super.init()
        locationService.delegate = self
        
        ///  Compute UI strings
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .removeDuplicates()
            .sink { state in
                self.alarmButtonText = state.alarmEnabled ? "Disable alarm" : "Enable Alarm"
            }
            .store(in: &cancellables)
        
        // TODO DRY + This doesn't feel nice yet, but its better than having it in a .task in the view
        /// Listen to location updates and start live activity when alarm enabled
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .map { state in state.alarmEnabled }
            .removeDuplicates()
            .sink { alarmEnabled in
                Task {
                    if (alarmEnabled){
                        /// TODO Location should already be listening if the app if foregrounded, so i don't think this is required
                        self.locationService.checkLocationPermissionsAndStartListening()
                        await LiveActivityManager.shared.start(
                            newDistanceToAlarm: self.state.distanceToGeofencePerimeter?.intValue,
                            alarmTriggered: self.state.alarmTriggered
                        )
                    } else {
                        await LiveActivityManager.shared.stop()
                    }
                }
            }
            .store(in: &cancellables)
        
        // TODO DRY + This doesn't feel nice yet, but its better than having it in a .task in the view
        /// Update live activity while alarm enbled
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
                    await LiveActivityManager.shared.updateActivity(
                        newDistanceToAlarm: distanceToAlarm,
                        alarmTriggered: holder.alarmTriggered
                    )
                }
            }
            .store(in: &cancellables)
        
        /// Sound alarm and vibrate when alarm triggered
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
    
    func onLocationPermissionChanged(state: PermissionState) {
        onLocationPermissionResult(state: state)
    }
    
    func onTapAllowLocationPermissions() {
        locationService.requestPermissions()
    }
    
    func requestNotificationPermissions()  {
        Task {
            let center = UNUserNotificationCenter.current()
            do {
                let granted = try await center.requestAuthorization(options: [.alert, .sound, .badge])
                logger.debug("Notification permissions granted: \(granted)")
                await MainActor.run {
                    onNotificationPermissionResult(granted: granted)
                }
            } catch {
                logger.debug("Error requesting notification permissions: \(error)")
            }
        }
    }
    
    /// Request location updates when map is open to update the geofence location initially
    func onViewDidAppear() {
        locationService.checkLocationPermissionsAndStartListening()
    }
    
    /// If the alarm isn't enabled, stop listening for location updates
    func onViewDidDissapear() {
        if (!state.alarmEnabled){
            locationService.stopListeningForUpdates()
        }
    }
}

// TODO This is annoying to have to have, but i couldn't work out how to use a tuple with the publisher chain
private struct DistanceAndTriggered: Equatable {
    let distanceToGeofencePerimeter: Int?
    let alarmTriggered: Bool
}
