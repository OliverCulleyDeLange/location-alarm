import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct DebugScreen: View {
    @StateViewModel private var viewModel: DebugViewModel = get()
    @State private var atBottom: Bool = true
    var body: some View {
        TabView {
            Logs(logUiState: viewModel.logUiState)
                .tabItem {
                    Label("Logs", systemImage: "list.bullet")
                }
            
            Gps(gpsUiState: viewModel.gpsUiState)
                .tabItem {
                    Label("Gps", systemImage: "location")
                }
            
            Tools()
                .tabItem {
                    Label("Tools", systemImage: "wrench.and.screwdriver")
                }
        }
    }
}
