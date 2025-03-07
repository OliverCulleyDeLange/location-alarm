import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI
@_spi(Experimental) import MapboxMaps

struct MapScreenContent: View {
    let state: MapUiState
    let onEvent: (UiEvents) -> Void
    
    var body: some View {
        ZStack {
            MapboxMap(
                geofenceLocation:state.geoFenceLocation,
                usersLocationToFlyTo:state.usersLocationToFlyTo,
                perimeterRadiusMeters: Double(state.perimeterRadiusMeters),
                onMapTap: { onEvent(UserEventTappedMap(location: $0)) },
                onZoomedToUserLocation: { onEvent(UiResultFinishedFLyingToUsersLocation()) }
            )
            
            HStack {
                Spacer()
                RadiusScrubber(
                    radiusMeters: state.perimeterRadiusMeters,
                    onRadiusChanged: { radius in
                        onEvent(UserEventDraggedRadiusControl(radius: radius))
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
                    .onTapGesture { onEvent(UserEventTappedLocationIcon()) }
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
                if (state.shouldShowDebugTools){
                    Button("Delayed Start", action: { onEvent(UserEventToggledAlarmWithDelay()) })
                        .padding(EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16))
                    
                    NavigationLink {
                        DebugScreen()
                    } label:{
                        Text("Debug Screen")
                            .padding(EdgeInsets(top: 8, leading: 16, bottom: 24, trailing: 16))
                    }
                }
                
                Button(action: { onEvent(UserEventToggledAlarm()) }) {
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
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .ignoresSafeArea()
    }
}


struct MapScreenContent_Previews: PreviewProvider {
    static var previews: some View {
        MapScreenContent(state: Shared.MapUiState(), onEvent: {_ in})
    }
}

