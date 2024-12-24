package uk.co.oliverdelange.locationalarm.logging

import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.model.ui.MapUiState
import kotlin.reflect.KProperty1

fun stateChangeLog(oldState: AppState, newState: AppState) = stateChangeLog(appStateTrackedProperties, oldState, newState)
fun stateChangeLog(oldState: MapUiState, newState: MapUiState) = stateChangeLog(mapUiStateTrackedProperties, oldState, newState)

private fun <T> stateChangeLog(trackedProperties: List<KProperty1<T, Any?>>, oldState: T, newState: T): List<String>? {
    val changes = trackedProperties.mapNotNull { prop ->
        val old = prop.get(oldState)
        val new = prop.get(newState)
        if (old != new) {
            "${prop.name}: $old -> $new"
        } else null
    }
    return changes.ifEmpty { null }
}

private val mapUiStateTrackedProperties = listOf(
    MapUiState::screenState,
    MapUiState::shouldShowAlarmAlert,
    MapUiState::toggleAlarmButtonText,
    MapUiState::enableAlarmButtonEnabled,
    MapUiState::shouldRequestNotificationPermissions,
    MapUiState::shouldShowNotificationPermissionDeniedMessage,
    MapUiState::shouldShowNotificationPermissionRationale,
    MapUiState::shouldRequestLocationPermissions,
    MapUiState::shouldEnableMapboxLocationComponent,
//    MapUiState::usersLocation,
//    MapUiState::geoFenceLocation,
    MapUiState::usersLocationToFlyTo,
    MapUiState::perimeterRadiusMeters,
    MapUiState::shouldShowDistanceToAlarmText,
    MapUiState::distanceToAlarmText,
)

private val appStateTrackedProperties = listOf(
    AppState::navigateTo,
    AppState::currentScreen,
    AppState::appInForeground,
    AppState::shouldRequestNotificationPermissions,
    AppState::notificationPermissionState,
    AppState::shouldRequestLocationPermissions,
    AppState::locationPermissionState,
    AppState::shouldListenForLocationUpdates,
//    AppState::usersLocation,
//    AppState::usersLocationHistory,
//    AppState::geoFenceLocation,
    AppState::mapInteracted,
    AppState::perimeterRadiusMeters,
    AppState::userRequestedAlarmEnable,
    AppState::alarmEnabled,
    AppState::alarmEnabledAt,
    AppState::alarmTriggered,
//    AppState::distanceToGeofencePerimeter,
//    AppState::distanceToGeofence,
    AppState::usersLocationToFlyTo,
)