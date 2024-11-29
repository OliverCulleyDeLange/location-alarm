import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI
@_spi(Experimental) import MapboxMaps

struct ContentView: View, LocationService.LocationServiceDelegate {
    @StateViewModel var viewModel = AppViewModel()
    @State private var showContent = false
    @State var viewport: Viewport = .followPuck(zoom: 16)
    @State var locationService: LocationService = LocationService()
    
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
                    logger.debug("Map did appear")
                    locationService.checkLocationPermissionsAndStartListening()
                }.onDisappear{
                    logger.debug("Map did dissapear")
                    locationService.stopListeningForUpdates()
                }
                .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                    logger.debug("Did receive UIApplication.didBecomeActiveNotification")
                    locationService.checkLocationPermissionsAndStartListening()
                }
                .ignoresSafeArea()
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
        .onAppear {
            locationService.delegate = self
        }
    }

    func onLocationUpdate(locations: Array<Shared.Location>) {
        viewModel.onLocationChange(locations: locations)
    }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
