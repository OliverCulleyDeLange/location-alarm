import SwiftUI
import Shared

struct NotificationPermissionDeniedAlert: View {    
    var body: some View {
        VStack(alignment: .leading) {
            Text("Notification Permission Denied"
            ).foregroundStyle(Color(.error))
                .font(.title2)
            Spacer().frame(height: 16)
            Text(Shared.MapScreenStrings.shared.notificationPermissionDeniedText
            ).foregroundStyle(Color(.onErrorContainer))
                .font(.body)
            Spacer().frame(height: 16)
            Text("Go to settings to allow notification permissions"
            ).foregroundStyle(Color(.onErrorContainer))
                .font(.body)
        }
        .padding(16)
        .background(Color(.errorContainer))
        .cornerRadius(8)
        .padding(.vertical, 8)
        .padding(.horizontal, 40)
    }
}

#Preview {
    NotificationPermissionDeniedAlert()
}
