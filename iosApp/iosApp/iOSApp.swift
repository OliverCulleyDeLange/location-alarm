import SwiftUI
import KMPObservableViewModelSwiftUI

@main
struct iOSApp: App {
    @StateViewModel private var viewModel = AppViewModel()
    
    var body: some Scene {
        WindowGroup {

            MapScreen(
                viewModel: viewModel
            )
                .onOpenURL { url in
                    if url.scheme == "uk.co.oliverdelange.locationalarm" {
                        logger.info("Deeplink: \(url)")
                        viewModel.onSetAlarm(enabled: false)
                    }
                }
        }
    }
}
