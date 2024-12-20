import Shared

/// Listens to app state and starts/stops listening for locatiopn updates appropriately
class LocationStateListener {
    private let locationService: LocationService
    private let appStateStore: Shared.AppStateStore
    
    init(locationService: LocationService, appStateStore: Shared.AppStateStore) {
        self.locationService = locationService
        self.appStateStore = appStateStore
    }
    
    
}
