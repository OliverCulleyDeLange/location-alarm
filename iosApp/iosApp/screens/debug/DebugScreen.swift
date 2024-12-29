import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct DebugScreen: View {
    @StateViewModel private var viewModel: DebugViewModel = get()
    
    var body: some View {
        ScrollView{
            LazyVStack(alignment: .leading) {
                ForEach(viewModel.state.logs, id: \.self) {
                    Text("\($0.date) \($0.message)")
                        .font(.caption2)
                }
            }
        }
    }
}
