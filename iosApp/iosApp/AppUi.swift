import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct AppUi: View {
    private var appStateStore: AppStateStore
    private var alarmManager: AlarmManager
    
    init(appStateStore: AppStateStore, alarmManager: AlarmManager) {
        self.appStateStore = appStateStore
        self.alarmManager = alarmManager
    }
        
    var body: some View {
        NavigationView {
            // TODO This sucks, but i wanna support ios15 and SwiftUI Navigation sucks. 
            switch appStateStore.state.currentScreen {
            case is RouteMapScreen:
                MapScreen()
            case is RouteLocationPermissionDeniedScreen:
                LocationPermissionsDeniedScreen()
            default:
                LocationPermissionsRequiredScreen()
            }
        }
    }
}
