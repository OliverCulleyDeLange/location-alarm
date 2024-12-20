import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI
@_spi(Experimental) import MapboxMaps

struct MapScreen: View {
    var viewModel: MapViewModelInterface
    
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
                    .padding(EdgeInsets(top: 0, leading: 24, bottom: 32, trailing: 0))
                    .onTapGesture { viewModel.onTapLocationIcon() }
                    .ignoresSafeArea()
                
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            VStack(alignment: .trailing) {
                Spacer()
                if (viewModel.state.shouldShowNotificationPermissionDeniedMessage){
                    NotificationPermissionDeniedAlert()
                }
                
                if (viewModel.state.shouldShowDistanceToAlarmText){
                    Text(viewModel.state.distanceToAlarmText)
                    .font(.caption)
                    .padding(8)
                    .background(Color(.primaryContainer))
                    .cornerRadius(8)
                    .padding(EdgeInsets(top: 8, leading: 16, bottom: 0, trailing: 16))
                }
                
                // Dev tool to enable alarm but not trigger for a time period to allow background / locked testing
                #if DEBUG
                Button("Delayed Start", action: { viewModel.onToggleAlarmWithDelay()})
                    .padding(EdgeInsets(top: 8, leading: 16, bottom: 24, trailing: 16))
                #endif
                
                Button(action: {viewModel.onToggleAlarm()}) {
                    Text(viewModel.state.toggleAlarmButtonText)
                        .foregroundStyle(Color(.primaryContainer))
                        .font(.system(size: 20, weight: .semibold))
                }
                .buttonStyle(.borderedProminent)
                .padding(EdgeInsets(top: 8, leading: 16, bottom: 24, trailing: 16))
                .disabled(!viewModel.state.enableAlarmButtonEnabled)
                
            }
            .frame(maxWidth: .infinity, alignment: .trailing)
            
        }
        .alert(isPresented: .constant(viewModel.state.shouldShowAlarmAlert)) {
            Alert(
                title: Text("Wakey Wakey"),
                message: Text("You have reached your destination."),
                dismissButton: .default(Text("Stop Alarm")){
                    logger.debug("Tapped Stop Alarm")
                    viewModel.onTapStopAlarm()
                }
            )
        }
        .onAppear {
            logger.debug("Map did appear")
            viewModel.onMapShown()
        }
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
            logger.debug("Did receive UIApplication.didBecomeActiveNotification")
            viewModel.onMapShown()
        }
        .onDisappear{
            logger.debug("Map did dissapear")
            viewModel.onMapNotShown()
        }
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.didEnterBackgroundNotification)) { _ in
            logger.debug("Did receive UIApplication.didEnterBackgroundNotification")
            viewModel.onMapNotShown()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .ignoresSafeArea()
    }
}


struct MapScreen_Previews: PreviewProvider {
    static var previews: some View {
        MapScreen(viewModel: FakeMapViewModel())
    }
}

class FakeMapViewModel : Shared.MapViewModelInterface {}
