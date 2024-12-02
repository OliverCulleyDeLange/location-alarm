import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI
@_spi(Experimental) import MapboxMaps

struct ContentView: View, LocationService.LocationServiceDelegate {
    @StateViewModel var viewModel = AppViewModel()
    @State var locationService: LocationService = LocationService()
    
    var body: some View {
        VStack {
            Text("SwiftUI: \(Greeting().greet())")
            ZStack {
                MapboxMap(
                    geofenceLocation: viewModel.state.geoFenceLocation,
                    perimeterRadiusMeters: Double(viewModel.state.perimeterRadiusMeters),
                    onMapTap: {viewModel.onMapTap(location: $0)}
                )
                HStack {
                    Spacer()
                    RadiusScrubber(
                        radiusMeters: viewModel.state.perimeterRadiusMeters,
                        onRadiusChanged: { radius in
                            viewModel.onRadiusChanged(radius: radius)
                        }
                    )
                        .containerRelativeFrame(.horizontal, count: 5, span: 1, spacing: 0)
                }
            }
            .onAppear {
                logger.debug("Map did appear")
                locationService.delegate = self
                locationService.checkLocationPermissionsAndStartListening()
            }.onDisappear{
                logger.debug("Map did dissapear")
                locationService.stopListeningForUpdates()
            }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                logger.debug("Did receive UIApplication.didBecomeActiveNotification")
                locationService.checkLocationPermissionsAndStartListening()
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
        
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
