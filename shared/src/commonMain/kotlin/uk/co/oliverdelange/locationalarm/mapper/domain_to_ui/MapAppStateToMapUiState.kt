package uk.co.oliverdelange.locationalarm.mapper.domain_to_ui

import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState
import uk.co.oliverdelange.locationalarm.model.domain.denied
import uk.co.oliverdelange.locationalarm.model.domain.granted
import uk.co.oliverdelange.locationalarm.model.domain.shouldShowRationale
import uk.co.oliverdelange.locationalarm.model.ui.map.MapUiScreenState
import uk.co.oliverdelange.locationalarm.model.ui.map.MapUiState

/** TODO Get strings from string provider
 * TODO UNIT TESTS
 * */
class MapAppStateToMapUiState {
    fun map(state: AppState): MapUiState {
        return MapUiState(
            screenState = when {
                state.locationPermissionState.granted() -> MapUiScreenState.ShowMap
                state.locationPermissionState.denied() -> MapUiScreenState.LocationPermissionDenied
                else -> MapUiScreenState.LocationPermissionRequired
            },
            shouldShowAlarmAlert = state.alarmTriggered,
            toggleAlarmButtonText = if (state.alarmEnabled) "Disable Alarm" else "Enable Alarm",
            enableAlarmButtonEnabled = state.notificationPermissionState !is PermissionState.Denied &&
                state.usersLocation != null &&
                state.geoFenceLocation != null,
            shouldRequestNotificationPermissions = state.shouldRequestNotificationPermissions,
            shouldShowNotificationPermissionDeniedMessage = state.notificationPermissionState is PermissionState.Denied,
            shouldShowNotificationPermissionRationale = state.notificationPermissionState.shouldShowRationale(),
            shouldRequestLocationPermissions = state.shouldRequestLocationPermissions,
            shouldEnableMapboxLocationComponent = state.locationPermissionState.granted(),
            shouldShowDebugTools = state.debug,
            usersLocation = state.usersLocation,
            geoFenceLocation = state.geoFenceLocation,
            usersLocationToFlyTo = state.usersLocationToFlyTo,
            perimeterRadiusMeters = state.perimeterRadiusMeters,
            shouldShowDistanceToAlarmText = state.alarmEnabled,
            distanceToAlarmText = if (state.alarmEnabled) {
                state.distanceToGeofencePerimeter?.let {
                    "${it}m to alarm"
                } ?: "Alarm active"
            } else ""
        )
    }
}