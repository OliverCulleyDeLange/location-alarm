import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct LocationPermissionsRequiredScreen: View {
    @StateViewModel var viewModel: LocationPermissionRequiredViewModel = get()
    
    var body: some View {
        VStack {
            Text("This app needs your location to enable location based alarms. Please allow precise location access for the app to work."
            ).foregroundStyle(Color(.primary))
                .padding()
                .multilineTextAlignment(.center)
            Spacer().frame(height: 24)
            Button(action: { viewModel.onEvent(uiEvent: UserEventTappedAllowLocationPermissions()) }) {
                Text("Allow Location Access")
            }
        }
    }
}


#Preview("LocationRequired") {
    LocationPermissionsRequiredScreen()
}
