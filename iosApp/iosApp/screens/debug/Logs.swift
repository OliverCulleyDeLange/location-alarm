import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct Logs: View {
    var logUiState: LogUiState
    @State private var atBottom: Bool = true
    
    var body: some View {
        ScrollViewReader { scrollView in
            ScrollView{
                LazyVStack(alignment: .leading) {
                    ForEach(logUiState.logs, id: \.self) { log in
                        let logColor = getLogColor(log.color)
                        ZStack {
                            Text(log.date + "\t")
                                .font(.caption2)
                                .fontWeight(.bold)
                            + Text(log.message)
                                .font(.caption)
                                .foregroundColor(logColor)
                        }
                        .id(log)
                    }
                    Color.clear
                        .frame(width: 0, height: 0, alignment: .bottom)
                        .onAppear { atBottom = true }
                        .onDisappear { atBottom = false }
                }.padding(8)
                    .onChange(of: logUiState.logs) { _ in
                        // Automatically scroll to the bottom when items change
                        if let lastItem = logUiState.logs.last {
                            if (atBottom) { scrollView.scrollTo(lastItem, anchor: .bottom) }
                        }
                    }
            }
        }
    }
    
    func getLogColor(_ logColor: LogColor) -> Color {
        switch(logColor){
        case .red:
            return Color(.red)
        case .orange:
            return Color(.orange)
        case .green:
            return Color(.green)
        case .blue:
            return Color(.blue)
        case .grey:
            return Color(.grey)
        default:
            return Color(.black)
        }
    }
}

#Preview {
    Logs(logUiState: LogUiState())
}



