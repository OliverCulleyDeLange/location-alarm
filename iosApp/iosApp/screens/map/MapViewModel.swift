import Shared
import UserNotifications
import Foundation
import Combine
import KMPNativeCoroutinesCombine


/// App side view model. Extends the shared kotlin view model and adds IOS specific functions.
class MapViewModel: Shared.MapViewModel, Cancellable, LocationService.LocationServiceDelegate {
    private var locationService = LocationService()
    private var notificationManager = NotificationManager.shared

    private var cancellables = Set<AnyCancellable>()
    
    override init(appStateStore: AppStateStore, uiStateMapper: MapAppStateToMapUiState) {
        super.init(appStateStore: appStateStore, uiStateMapper: uiStateMapper)
        locationService.delegate = self
        
        listenAndRequestLocationPermissions()
        listenAndRequestNotificationPermissions()
    }
    
    func cancel() {
        for c in cancellables {
            c.cancel()
        }
        cancellables = []
    }
    
    // Overridden LocationService delegate methods simply pass through to viewmodel
    func onLocationUpdate(locations: Array<Shared.Location>) {
        onLocationChange(locations: locations)
    }
    
    // Overridden LocationService delegate methods simply pass through to shared viewmodel
    func onLocationPermissionChanged(state: PermissionState) {
        onLocationPermissionResult(state: state)
    }
    
    func requestNotificationPermissions()  {
        notificationManager.requestPermissions{ granted in
            self.onNotificationPermissionResult(granted: granted)
        }
    }
    
    /// Request location updates when map is open to update the geofence location initially
    func onMapViewDidAppear() {
        onMapShown()
        locationService.checkLocationPermissionsAndStartListening()
        notificationManager.checkPermissions{ state in
            self.onNotificationPermissionResult(state: state)
        }
    }
    
    /// If the alarm isn't enabled, stop listening for location updates
    func onMapViewDidDissapear() {
        onMapNotShown()
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
}
