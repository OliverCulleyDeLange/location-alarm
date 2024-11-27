import SwiftUI
import Shared
@_spi(Experimental) import MapboxMaps

struct ContentView: View {
    @State private var showContent = false
    @State var viewport: Viewport = .followPuck(zoom: 16)
    @State var locationProvider: AppleLocationProvider = AppleLocationProvider()
    @State var locationObserver: AnyCancelable? = nil
    
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
            
            MapReader { map in
                Map(viewport: $viewport){
                    Puck2D(bearing: .heading).showsAccuracyRing(true)
                }.onAppear {
                    // Start listening for location updates when map is ready
                    locationObserver = locationProvider.onLocationUpdate.observe { location in
                        print("Location  \(location[0].coordinate)")
                    }
                }.onDisappear{
                    locationObserver?.cancel()
                }
                .ignoresSafeArea()
            }
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
