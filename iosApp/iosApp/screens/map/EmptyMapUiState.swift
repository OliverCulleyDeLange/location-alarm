import Shared

// TODO Apparently the swift code should have a default initaliser with default values from kotlin but this doesn't seem to work.
extension Shared.MapUiState {
    convenience init(screenState: MapUiScreenState = MapUiScreenState.showmap) {
        self.init(screenState: screenState, shouldShowAlarmAlert: false, toggleAlarmButtonText: "Enable Alarm", enableAlarmButtonEnabled: true, shouldRequestNotificationPermissions: false, shouldShowNotificationPermissionDeniedMessage: false, shouldShowNotificationPermissionRationale: false, shouldRequestLocationPermissions: false, shouldEnableMapboxLocationComponent: false, usersLocation: nil, geoFenceLocation: nil, usersLocationToFlyTo: nil, perimeterRadiusMeters: 200, shouldShowDistanceToAlarmText: true, distanceToAlarmText: "100m to alarm")
    }
}
