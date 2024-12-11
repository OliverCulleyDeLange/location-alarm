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
            Text("Alarm: \(viewModel.state.alarmTriggered)")
            ZStack {
                MapboxMap(
                    geofenceLocation: viewModel.state.geoFenceLocation,
                    perimeterRadiusMeters: Double(viewModel.state.perimeterRadiusMeters),
                    onMapTap: {viewModel.onMapTap(newGeofenceLocation: $0)}
                )
                
                HStack {
                    Spacer()
                    RadiusScrubber(
                        radiusMeters: viewModel.state.perimeterRadiusMeters,
                        onRadiusChanged: { radius in
                            viewModel.onRadiusChanged(radius: radius)
                        }
                    )
                }
                
                VStack {
                    Spacer()
                    Image(systemName: "location.circle")
                        .resizable()
                        .frame(width: 40, height: 40)
                        .foregroundStyle(Color(.primary))
                        .padding(16)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                
                VStack {
                    Spacer()
                    
                    VStack(alignment: .leading) {
                        // FIXME Move strings to viewmodel & handle optionalness
                        Text("\(viewModel.state.distanceToGeofence?.stringValue ?? "?")m -> Destination")
                            .font(.caption)
                        Text("\(viewModel.state.distanceToGeofencePerimeter?.stringValue ?? "?")m -> Alarm")
                            .font(.caption)
                    }
                    .padding(8)
                    .background(Color(.primaryContainer))
                    .cornerRadius(8)
                    .padding(EdgeInsets(top: 8, leading: 16, bottom: 0, trailing: 16))
                    
                    Button(action: { viewModel.onToggleAlarm()}){
                        Text(viewModel.alarmButtonText)
                            .foregroundStyle(Color(.primaryContainer))
                            .fontWeight(.semibold)
                    }.buttonStyle(.borderedProminent)
                        .padding(EdgeInsets(top: 8, leading: 16, bottom: 16, trailing: 16))
                }
                .frame(maxWidth: .infinity, alignment: .trailing)

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
