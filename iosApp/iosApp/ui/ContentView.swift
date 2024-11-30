import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI
@_spi(Experimental) import MapboxMaps

struct ContentView: View, LocationService.LocationServiceDelegate {
    @StateViewModel var viewModel = AppViewModel()
    @State var viewport: Viewport = .followPuck(zoom: 16)
    @State var locationService: LocationService = LocationService()
    
    var body: some View {
        VStack {
            Text("SwiftUI: \(Greeting().greet())")
            
            MapReader { map in
                Map(viewport: $viewport)
                {
                    Puck2D(bearing: .heading).showsAccuracyRing(true)
                    if let geofence = viewModel.state.geoFenceLocation {
                        let circle = createCirclePolygon(center: geofence.toCLLocationCoordinate2D(), radius: Double(viewModel.state.perimeterRadiusMeters))
                        
                        GeoJSONSource(id: MapboxIDs.shared.SOURCE_GEOFENCE)
                            .data(.feature(Feature(geometry: .polygon(circle))))
                        FillLayer(id: MapboxIDs.shared.LAYER_GEOFENCE_FILL, source: MapboxIDs.shared.SOURCE_GEOFENCE)
                            .fillColor(UIColor(named: "GeofenceLine")!)
                            .fillOpacity(0.3)
                        LineLayer(id: MapboxIDs.shared.LAYER_GEOFENCE_LINE, source: MapboxIDs.shared.SOURCE_GEOFENCE)
                            .lineWidth(5.0)
                            .lineColor(UIColor(named: "GeofenceLine")!)
                    }
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

