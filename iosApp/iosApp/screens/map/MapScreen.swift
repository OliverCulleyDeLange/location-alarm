import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI
@_spi(Experimental) import MapboxMaps

struct ContentView: View, LocationService.LocationServiceDelegate {
    @StateViewModel var viewModel = AppViewModel()
    @State var locationService: LocationService = LocationService()
    @State var alarmManager: AlarmManager = AlarmManager()

    
    var body: some View {
        ZStack {
            MapboxMap(
                geofenceLocation: viewModel.state.geoFenceLocation,
                usersLocationToFlyTo: viewModel.state.usersLocationToFlyTo,
                perimeterRadiusMeters: Double(viewModel.state.perimeterRadiusMeters),
                onMapTap: {viewModel.onMapTap(newGeofenceLocation: $0)},
                onZoomedToUserLocation: { viewModel.onFinishFlyingToUsersLocation() }
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
                    .padding(EdgeInsets(top: 0, leading: 24, bottom: 24, trailing: 0))
                    .onTapGesture { viewModel.onTapLocationIcon() }
                    .ignoresSafeArea()
                
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
                    .padding(EdgeInsets(top: 8, leading: 16, bottom: 24, trailing: 16))
            }
            .frame(maxWidth: .infinity, alignment: .trailing)
            
        }
        .alert(isPresented: .constant(viewModel.state.alarmTriggered)) {
            Alert(
                title: Text("Wakey Wakey"),
                message: Text("You have reached your destination."),
                dismissButton: .default(Text("Stop Alarm")){
                    viewModel.onSetAlarm(enabled: false)
                }
            )
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
        // TODO I'm not sure about these stacked tasks - they feel a bit clunky code cleanliness wise.
        // Maybe Just extracting the functions out will help?
        // There's probably a smarter way to use the observable state here
        .task(id: viewModel.state.userRequestedAlarmEnable) {
            if(
                viewModel.state.userRequestedAlarmEnable &&
                viewModel.state.notificationPermissionState != Shared.PermissionState.granted
            ){
                let center = UNUserNotificationCenter.current()
                do {
                    let granted = try await center.requestAuthorization(options: [.alert, .sound, .badge])
                    logger.debug("Notification permissions granted: \(granted)")
                    viewModel.onNotificationPermissionResult(granted: granted)
                } catch {
                    logger.debug("Error requesting notification permissions: \(error)")
                }
            }
        }
        .task(id: viewModel.state.alarmTriggered) {
            if (viewModel.state.alarmTriggered){
                alarmManager.startAlarm()
            } else {
                alarmManager.stopAlarm()
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .ignoresSafeArea()
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
