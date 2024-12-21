import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI
@_spi(Experimental) import MapboxMaps

struct MapScreen: View {
    
    let state: MapUiState
    let callbacks: MapScreenCallbacks
    
    var body: some View {
        ZStack {
            MapboxMap(
                geofenceLocation:state.geoFenceLocation,
                usersLocationToFlyTo:state.usersLocationToFlyTo,
                perimeterRadiusMeters: Double(state.perimeterRadiusMeters),
                onMapTap: { callbacks.onMapTap(newGeofenceLocation: $0)},
                onZoomedToUserLocation: { callbacks.onFinishFlyingToUsersLocation() }
            )
            
            HStack {
                Spacer()
                RadiusScrubber(
                    radiusMeters: state.perimeterRadiusMeters,
                    onRadiusChanged: { radius in
                        callbacks.onRadiusChanged(radius: radius)
                    }
                )
            }
            
            VStack {
                Spacer()
                Image(systemName: "location.circle")
                    .resizable()
                    .frame(width: 40, height: 40)
                    .foregroundStyle(Color(.primary))
                    .padding(EdgeInsets(top: 0, leading: 24, bottom: 32, trailing: 0))
                    .onTapGesture { callbacks.onTapLocationIcon() }
                    .ignoresSafeArea()
                
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            VStack(alignment: .trailing) {
                Spacer()
                if (state.shouldShowNotificationPermissionDeniedMessage){
                    NotificationPermissionDeniedAlert()
                }
                
                if (state.shouldShowDistanceToAlarmText){
                    Text(state.distanceToAlarmText)
                        .font(.caption)
                        .padding(8)
                        .background(Color(.primaryContainer))
                        .cornerRadius(8)
                        .padding(EdgeInsets(top: 8, leading: 16, bottom: 0, trailing: 16))
                }
                
                // Dev tool to enable alarm but not trigger for a time period to allow background / locked testing
#if DEBUG
                Button("Delayed Start", action: { callbacks.onToggleAlarmWithDelay()})
                    .padding(EdgeInsets(top: 8, leading: 16, bottom: 24, trailing: 16))
#endif
                
                Button(action: {callbacks.onToggleAlarm()}) {
                    Text(state.toggleAlarmButtonText)
                        .foregroundStyle(Color(.primaryContainer))
                        .font(.system(size: 20, weight: .semibold))
                }
                .buttonStyle(.borderedProminent)
                .padding(EdgeInsets(top: 8, leading: 16, bottom: 24, trailing: 16))
                .disabled(!state.enableAlarmButtonEnabled)
                
            }
            .frame(maxWidth: .infinity, alignment: .trailing)
            
        }
        .alert(isPresented: .constant(state.shouldShowAlarmAlert)) {
            Alert(
                title: Text("Wakey Wakey"),
                message: Text("You have reached your destination."),
                dismissButton: .default(Text("Stop Alarm")){
                    logger.debug("Tapped Stop Alarm")
                    callbacks.onTapStopAlarm()
                }
            )
        }
        .onAppear {
            logger.debug("Map did appear")
            callbacks.onMapViewDidAppear()
        }
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
            logger.debug("Did receive UIApplication.didBecomeActiveNotification")
            callbacks.onMapViewDidAppear()
        }
        .onDisappear{
            logger.debug("Map did dissapear")
            callbacks.onMapViewDidDissapear()
        }
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.didEnterBackgroundNotification)) { _ in
            logger.debug("Did receive UIApplication.didEnterBackgroundNotification")
            callbacks.onMapViewDidDissapear()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .ignoresSafeArea()
    }
}


struct MapScreen_Previews: PreviewProvider {
    static var previews: some View {
        MapScreen(state: Shared.MapUiState(), callbacks: EmptyMapScreenCallbacks())
    }
}

extension Shared.MapUiState {
    convenience init(screenState: MapUiScreenState = MapUiScreenState.showmap) {
        self.init(screenState: screenState, shouldShowAlarmAlert: false, toggleAlarmButtonText: "Enable Alarm", enableAlarmButtonEnabled: true, shouldRequestNotificationPermissions: false, shouldShowNotificationPermissionDeniedMessage: false, shouldShowNotificationPermissionRationale: false, shouldRequestLocationPermissions: false, shouldEnableMapboxLocationComponent: false, usersLocation: nil, geoFenceLocation: nil, usersLocationToFlyTo: nil, perimeterRadiusMeters: 200, shouldShowDistanceToAlarmText: true, distanceToAlarmText: "100m to alarm")
    }
}
