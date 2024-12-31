import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct Tools: View {

    var body: some View {
        VStack {
            Button {
                fatalError("This is a forced crash for testing purposes.")
            } label: {
                Text("Crash")
            }
        }
    }
}

#Preview {
    Tools()
}
