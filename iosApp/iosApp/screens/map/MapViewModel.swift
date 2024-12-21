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
    
    override func onEvent(uiEvent: Shared.UiEvents){
        switch uiEvent {
        case is UiResultMapShown:
            super.onEvent(uiEvent: uiEvent)
            locationService.checkLocationPermissionsAndStartListening()
            notificationManager.checkPermissions{ state in
                self.onEvent(uiEvent: UiResultNotificationPermissionResult(state: state))
            }
        default:
            super.onEvent(uiEvent: uiEvent)
        }
    }
    
    func cancel() {
        for c in cancellables {
            c.cancel()
        }
        cancellables = []
    }
    
    // Overridden LocationService delegate methods simply pass through to viewmodel
    func onLocationUpdate(locations: Array<Shared.Location>) {
        onEvent(uiEvent: UiResultLocationChanged(location: locations))
    }
    
    // Overridden LocationService delegate methods simply pass through to shared viewmodel
    func onLocationPermissionChanged(state: PermissionState) {
        onEvent(uiEvent: UiResultLocationPermissionResult(state: state))
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
    
    fileprivate func requestNotificationPermissions()  {
        notificationManager.requestPermissions{ granted in
            self.onEvent(uiEvent: UiResultNotificationPermissionResult(state: PermissionStateKt.permissionStateFrom(granted: granted)))
        }
    }
    
}
