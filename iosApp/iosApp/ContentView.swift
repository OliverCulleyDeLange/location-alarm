import SwiftUI
import Shared
@_spi(Experimental) import MapboxMaps

struct ContentView: View {
    @State private var showContent = false
    @State var viewport: Viewport = .followPuck(zoom: 16)

    var body: some View {
        VStack {
            Button("Click me!") {
                withAnimation {
                    showContent = !showContent
                }
            }

            if showContent {
                VStack(spacing: 16) {
                    Image(systemName: "swift")
                        .font(.system(size: 200))
                        .foregroundColor(.accentColor)
                    Text("SwiftUI: \(Greeting().greet())")
                }
                .transition(.move(edge: .top).combined(with: .opacity))
            }
            
            let center = CLLocationCoordinate2D(latitude: 39.5, longitude: -98.0)
            Map(viewport: $viewport){
                Puck2D(bearing: .heading).showsAccuracyRing(true)
            }
                .ignoresSafeArea()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
