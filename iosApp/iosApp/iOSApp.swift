import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

@main
struct iOSApp: App {
    @StateViewModel private var viewModel = AppViewModel()
    
    var body: some Scene {
        WindowGroup {
            if (viewModel.state.locationPermissionState == Shared.PermissionState.granted){
                MapScreen(
                    viewModel: viewModel
                )
                .onOpenURL { url in
                    if url.scheme == "uk.co.oliverdelange.locationalarm" {
                        logger.info("Deeplink: \(url)")
                        viewModel.onSetAlarm(enabled: false)
                    }
                }
            } else if (viewModel.state.locationPermissionState == Shared.PermissionState.unknown) {
                LocationPermissionsRequiredScreen {
                    viewModel.onTapAllowLocationPermissions()
                }
            } else {
                LocationPermissionsDeniedScreen()
            }
        }
    }
}
