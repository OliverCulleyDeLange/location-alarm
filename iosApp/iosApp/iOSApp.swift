import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

@main
struct iOSApp: App {
    @StateViewModel private var viewModel = AppViewModel()
    
    var body: some Scene {
        WindowGroup {
            if (viewModel.state.locationPermissionState is Shared.PermissionStateGranted){
                MapScreen(
                    viewModel: viewModel
                )
                .onOpenURL { url in
                    if url.scheme == "uk.co.oliverdelange.locationalarm" {
                        logger.info("Deeplink: \(url)")
                        viewModel.onSetAlarm(enabled: false)
                    }
                }
            } else if (viewModel.state.locationPermissionState is Shared.PermissionStateUnknown) {
                LocationPermissionsRequiredScreen {
                    viewModel.onTapAllowLocationPermissions()
                }
            } else {
                LocationPermissionsDeniedScreen()
            }
        }
    }
}
