import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI
@_spi(Experimental) import MapboxMaps

struct MapScreen: View {
    @StateViewModel var viewModel: AppViewModel
    
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
                
                if (viewModel.state.alarmEnabled){
                    // FIXME Move strings to viewmodel & handle optionalness
                    Text("\(viewModel.state.distanceToGeofencePerimeter?.stringValue ?? "?")m -> Alarm")
                    .font(.caption)
                    .padding(8)
                    .background(Color(.primaryContainer))
                    .cornerRadius(8)
                    .padding(EdgeInsets(top: 8, leading: 16, bottom: 0, trailing: 16))
                }
                
                // Dev tool to enable alarm but not trigger for a time period to allowe background / locked testing
                #if DEBUG
                Button("Delayed Start", action: { viewModel.onToggleAlarmWithDelay(locationUpdates: 3)})
                    .padding(EdgeInsets(top: 8, leading: 16, bottom: 24, trailing: 16))
                #endif
                
                Button(action: {viewModel.onToggleAlarm()}) {
                    Text(viewModel.alarmButtonText)
                        .foregroundStyle(Color(.primaryContainer))
                        .font(.system(size: 20, weight: .semibold))
                }
                .buttonStyle(.borderedProminent)
                .padding(EdgeInsets(top: 8, leading: 16, bottom: 24, trailing: 16))
                .disabled(!viewModel.state.enableAlarmButtonEnabled)
                
            }
            .frame(maxWidth: .infinity, alignment: .trailing)
            
        }
        .alert(isPresented: .constant(viewModel.state.alarmTriggered)) {
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
            viewModel.onViewDidAppear()
        }
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
            logger.debug("Did receive UIApplication.didBecomeActiveNotification")
            viewModel.onViewDidAppear()
        }
        .onDisappear{
            logger.debug("Map did dissapear")
            viewModel.onViewDidDissapear()
        }
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.didEnterBackgroundNotification)) { _ in
            logger.debug("Did receive UIApplication.didEnterBackgroundNotification")
            viewModel.onViewDidDissapear()
        }
        // TODO I'm not sure about these stacked tasks - they feel a bit clunky code cleanliness wise.
        // Maybe Just extracting the functions out will help?
        // There's probably a smarter way to use the observable state here
        .task(id: viewModel.state.userRequestedAlarmEnable) {
            if(
                viewModel.state.userRequestedAlarmEnable &&
                !(viewModel.state.notificationPermissionState is Shared.PermissionStateGranted)
            ){
                viewModel.requestNotificationPermissions()
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .ignoresSafeArea()
    }
}


struct MapScreen_Previews: PreviewProvider {
    static var previews: some View {
        MapScreen(viewModel: AppViewModel())
    }
}
