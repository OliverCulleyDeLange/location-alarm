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
                callbacks: viewModel
            )
            .onOpenURL { url in
                if url.scheme == "uk.co.oliverdelange.locationalarm" {
                    logger.info("Deeplink: \(url)")
                    viewModel.onSetAlarm(enabled: false)
                }
            }
        case .locationpermissionrequired:
            LocationPermissionsRequiredScreen {
                viewModel.onTapAllowLocationPermissions()
            }
        default:
            LocationPermissionsDeniedScreen()
        }
    }
}
