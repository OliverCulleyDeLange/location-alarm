import Shared
import UserNotifications
import Foundation
import Combine
import KMPNativeCoroutinesCombine

/// App side view model. Extends the shared kotlin view model and adds IOS specific functions.
class AppViewModel: Shared.AppViewModel, Cancellable, LocationService.LocationServiceDelegate {
    private var locationService = LocationService()
    private var alarmManager = AlarmManager.shared
    private var notificationManager = NotificationManager.shared
    
    private var cancellables = Set<AnyCancellable>()
    
    @Published var alarmButtonText: String  = "Enable alarm"
    @Published var distanceToAlarmText: String  = ""
    
    override init() {
        super.init()
        locationService.delegate = self
        
        computeUiStrings()
        soundAlarmAndVibrateWhenAlarmTriggered()
        listenAndRequestLocationPermissions()
        listenAndRequestNotificationPermissions()
        
        /// Listen to location updates and start live activity when alarm enabled
        if #available(iOS 16.2, *) {
            handleLiveActivityLifecycle()
            updateLiveActivityWhileAlarmActive()
        }
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
    
    func requestNotificationPermissions()  {
        notificationManager.requestPermissions{ granted in
            self.onNotificationPermissionResult(granted: granted)
        }
    }
    
    /// Request location updates when map is open to update the geofence location initially
    func onViewDidAppear() {
        locationService.checkLocationPermissionsAndStartListening()
        notificationManager.checkPermissions{ state in
            self.onNotificationPermissionResult(state: state)
        }
    }
    
    /// If the alarm isn't enabled, stop listening for location updates
    func onViewDidDissapear() {
        if (!state.alarmEnabled){
            locationService.stopListeningForUpdates()
        }
    }
    
    fileprivate func computeUiStrings() {
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .removeDuplicates()
            .sink { state in
                //FIXME seems kind pointless duplicating strings in both app projects
                // I know android has good string handling but ios doesn't really, so might make sense to handle in shared code
                self.alarmButtonText = state.alarmEnabled ? "Disable alarm" : "Enable Alarm"
                if let distanceToGeofencePerimeter = state.distanceToGeofencePerimeter{
                    self.distanceToAlarmText = "\(distanceToGeofencePerimeter)m to alarm"
                } else {
                    self.distanceToAlarmText = "Alarm active"
                }
            }
            .store(in: &cancellables)
    }
    
    fileprivate func listenAndRequestNotificationPermissions() {
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .map { $0.shouldRequestNotificationPermissions }
            .removeDuplicates()
            .filter { $0 }
            .sink { shouldRequestNotificationPermissions in
                self.requestNotificationPermissions()
            }
            .store(in: &cancellables)
    }
    
    fileprivate func listenAndRequestLocationPermissions() {
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .map { $0.shouldRequestLocationPermissions }
            .filter { $0 }
            .removeDuplicates()
            .sink { shouldRequestLocationPermissions in
                self.locationService.requestPermissions()
            }
            .store(in: &cancellables)
    }
    
    @available(iOS 16.2, *)
    fileprivate func handleLiveActivityLifecycle() {
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .map { state in state.alarmEnabled }
            .removeDuplicates()
            .sink { alarmEnabled in
                Task {
                    if (alarmEnabled){
                        /// TODO Location should already be listening if the app if foregrounded, so i don't think this is required
                        self.locationService.checkLocationPermissionsAndStartListening()
                        guard let distanceToAlarm = self.state.distanceToGeofencePerimeter else {
                            logger.warning("Trying to create live activity, but no available distance to alarm")
                            return
                        }
                        await LiveActivityManager.shared.createLiveActivity(
                            distanceToAlarm.intValue,
                            self.state.alarmTriggered
                        )
                    } else {
                        await LiveActivityManager.shared.stopLiveActivity()
                    }
                }
            }
            .store(in: &cancellables)
    }
    
    @available(iOS 16.2, *)
    fileprivate func updateLiveActivityWhileAlarmActive() {
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
                    await LiveActivityManager.shared.updatePersistentNotification(
                        distanceToAlarm,
                        holder.alarmTriggered
                    )
                }
            }
            .store(in: &cancellables)
    }
    
    fileprivate func soundAlarmAndVibrateWhenAlarmTriggered() {
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .map { $0.alarmTriggered }
            .removeDuplicates()
            .sink { alarmTriggered in
                if (alarmTriggered){
                    self.alarmManager.startAlarm()
                    self.notificationManager.createAlarmNotification()
                } else {
                    self.alarmManager.stopAlarm()
                    self.notificationManager.removeAlarmNotification()
                }
            }
            .store(in: &cancellables)
    }
}

// TODO This is annoying to have to have, but i couldn't work out how to use a tuple with the publisher chain
private struct DistanceAndTriggered: Equatable {
    let distanceToGeofencePerimeter: Int?
    let alarmTriggered: Bool
}
