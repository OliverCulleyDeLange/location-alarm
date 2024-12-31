import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

struct Gps: View {
    var gpsUiState: GpsUiState
    @State private var atBottom: Bool = true
    
    var body: some View {
        ScrollViewReader { scrollView in
            ScrollView{
                LazyVStack(alignment: .leading) {
                    ForEach(gpsUiState.gps, id: \.self) { gps in
                        Text("\(gps.date)")
                            .font(.caption2)
                            .id(gps)
                    }
                    Color.clear
                        .frame(width: 0, height: 0, alignment: .bottom)
                        .onAppear { atBottom = true }
                        .onDisappear { atBottom = false }
                }.padding(8)
                    .onChange(of: gpsUiState.gps) { _ in
                        // Automatically scroll to the bottom when items change
                        if let lastItem = gpsUiState.gps.last {
                            if (atBottom) { scrollView.scrollTo(lastItem, anchor: .bottom) }
                        }
                    }
            }
        }
    }
}

#Preview {
    Gps(gpsUiState: GpsUiState())
}
