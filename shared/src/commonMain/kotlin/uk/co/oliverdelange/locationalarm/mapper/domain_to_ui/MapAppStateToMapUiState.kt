package uk.co.oliverdelange.locationalarm.mapper.domain_to_ui

import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState
import uk.co.oliverdelange.locationalarm.model.domain.granted
import uk.co.oliverdelange.locationalarm.model.domain.shouldShowRationale
import uk.co.oliverdelange.locationalarm.model.ui.map.MapUiState
import uk.co.oliverdelange.locationalarm.strings.MapScreenStrings

/** TODO Get strings from string provider
 * TODO UNIT TESTS
 * */
class MapAppStateToMapUiState {
    fun map(state: AppState): MapUiState {
        return MapUiState(
            shouldShowAlarmAlert = state.alarmTriggered,
            toggleAlarmButtonText = if (state.alarmEnabled) MapScreenStrings.disableAlarm else MapScreenStrings.enableAlarm,
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