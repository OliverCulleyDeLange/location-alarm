import Shared
import Combine
import KMPNativeCoroutinesCombine

/// Listens to app state and starts/stops listening for location updates appropriately
class LocationStateListener: LocationService.LocationServiceDelegate {
    private let locationService: LocationService
    private let appStateStore: Shared.AppStateStore
    
    // TODO Do we need to cancel these? This should live for the lifecycle of the app...
    private var cancellables = Set<AnyCancellable>()
    
    init(locationService: LocationService, appStateStore: Shared.AppStateStore) {
        self.locationService = locationService
        self.appStateStore = appStateStore
        locationService.delegate = self
        
        listenAndListenForLocationUpdates()
        listenAndRequestLocationPermissions()
    }
    
    fileprivate func listenAndListenForLocationUpdates() {
        createPublisher(for: appStateStore.stateFlow)
            .assertNoFailure()
            .map { $0.shouldListenForLocationUpdates }
            .removeDuplicates()
            .sink { shouldListenForLocationUpdates in
                if (shouldListenForLocationUpdates){
                    self.locationService.checkLocationPermissionsAndStartListening()
                } else {
                    self.locationService.stopListeningForUpdates()
                }
            }
            .store(in: &cancellables)
    }
    
    fileprivate func listenAndRequestLocationPermissions() {
        createPublisher(for: appStateStore.stateFlow)
            .assertNoFailure()
            .map { $0.shouldRequestLocationPermissions }
            .filter { $0 }
            .removeDuplicates()
            .sink { shouldRequestLocationPermissions in
                self.locationService.requestPermissions()
            }
            .store(in: &cancellables)
    }
    
    // Overridden LocationService delegate methods simply pass through to viewmodel
    internal func onLocationUpdate(locations: Array<Shared.Location>) {
        appStateStore.onLocationChange(locations: locations)
    }
    
    // Overridden LocationService delegate methods simply pass through to shared viewmodel
    internal func onLocationPermissionChanged(state: PermissionState) {
        appStateStore.onLocationPermissionResult(state: state)
    }
}
