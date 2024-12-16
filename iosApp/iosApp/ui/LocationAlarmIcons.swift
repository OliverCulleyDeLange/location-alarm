import SwiftUI

struct LocationAlarmIcons: View {
    let alarmTriggered: Bool
    
    var body: some View {
        HStack {
            Image("Icon")
                .resizable()
                .frame(width: 24, height: 24)
                .foregroundStyle(.primary)
                .padding(.horizontal, 4)

            Image(systemName: alarmTriggered ? "exclamationmark.triangle.fill" : "checkmark.circle.fill")
                .resizable()
                .frame(width: 24, height: 24)
                .foregroundStyle(alarmTriggered ? .orange : .green)
                .padding(2)
        }
    }
}

#Preview("Icons"){
    LocationAlarmIcons(alarmTriggered: false)
}
