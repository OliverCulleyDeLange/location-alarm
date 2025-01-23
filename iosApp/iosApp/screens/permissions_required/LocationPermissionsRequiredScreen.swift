import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct LocationPermissionsRequiredScreen: View {
    @StateViewModel var viewModel: LocationPermissionRequiredViewModel = get()
    
    var body: some View {
        VStack {
            Text(
                MapScreenStrings.shared.locationPermissionRequiredText
            ).foregroundStyle(Color(.primary))
                .padding()
                .multilineTextAlignment(.center)
            Spacer().frame(height: 24)
            Button(action: { viewModel.onEvent(uiEvent: UserEventTappedAllowLocationPermissions()) }) {
                Text(MapScreenStrings.shared.allowLocationAccess)
            }
        }
    }
}


#Preview("LocationRequired") {
    LocationPermissionsRequiredScreen()
}
