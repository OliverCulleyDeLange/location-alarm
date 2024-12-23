import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct AppUi: View {
    private var appStateStore: AppStateStore
    @StateViewModel private var viewModel: MapViewModel
    private var alarmManager: AlarmManager
    
    
    init(appStateStore: AppStateStore, alarmManager: AlarmManager) {
        self.appStateStore = appStateStore
        self.alarmManager = alarmManager
        _viewModel = StateViewModel(
            wrappedValue: MapViewModel(appStateStore: appStateStore, uiStateMapper: MapAppStateToMapUiState())
        )
    }
    
    var body: some View {
        
        switch viewModel.state.screenState {
        case .showmap:
            MapScreen(
                state: viewModel.state,
                onEvent: { viewModel.onEvent(uiEvent: $0) }
            )
            .onOpenURL { url in
                if url.scheme == "uk.co.oliverdelange.locationalarm" {
                    SLog.i("Deeplink: \(url)")
                    switch url.relativePath {
                    case "action/stop_alarm":
                        viewModel.onEvent(uiEvent: UserEventOpenedDeepLinkStopAlarm())
                        
                    default:
                        SLog.w("Unhandled deeplink \(url.relativePath)")
                    }
                }
            }
        case .locationpermissionrequired:
            LocationPermissionsRequiredScreen {
                viewModel.onEvent(uiEvent: UserEventTappedAllowLocationPermissions())
            }
        default:
            LocationPermissionsDeniedScreen()
        }
    }
}
