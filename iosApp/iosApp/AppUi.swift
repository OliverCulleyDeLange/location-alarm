import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared
import Combine
import KMPNativeCoroutinesCombine

struct AppUi: View {
    private var appStateStore: AppStateStore
    private var alarmManager: AlarmManager
    @StateObject private var navViewModel: NavigationViewModel
    
    init(appStateStore: AppStateStore, alarmManager: AlarmManager) {
        self.appStateStore = appStateStore
        self.alarmManager = alarmManager
        _navViewModel = StateObject(wrappedValue: NavigationViewModel(statePublisher: createPublisher(for: appStateStore.stateFlow)))
    }
    
    var body: some View {
        NavigationView {
            switch navViewModel.currentScreen {
            case "MapScreen":
                MapScreen()
            case "LocationPermissionDeniedScreen":
                LocationPermissionsDeniedScreen()
            default:
                LocationPermissionsRequiredScreen()
            }
        }
    }
}
