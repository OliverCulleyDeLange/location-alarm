import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct LocationPermissionsDeniedScreen: View {
    var body: some View {
        VStack {
            Text("You have denied location permissions")
                .foregroundStyle(Color(.primary))
                .font(.system(size:20, weight: .bold))
                .padding()
                .multilineTextAlignment(.center)
            
            Spacer().frame(height: 16)

            Text("This app requires location permissions to wake you up when you're near your destination")
                .foregroundStyle(Color(.primary))
                .padding()
                .multilineTextAlignment(.center)
            
            Spacer().frame(height: 16)

            Text("Please go to settings to grant location permissions to this app")
                .foregroundStyle(Color(.primary))
                .padding()
                .multilineTextAlignment(.center)
        }
    }
}


#Preview("LocationDenied") {
    LocationPermissionsDeniedScreen()
}
