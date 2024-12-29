package uk.co.oliverdelange.locationalarm.logging

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import uk.co.oliverdelange.locationalarm.helper.doWhen
import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.model.ui.map.MapUiState
import kotlin.reflect.KProperty1

/** When debug flag is enabled, we compute state changes and log them to aid in debugging things */
class AppStateChangeLogger(
    private val debug: Flow<Boolean>,
    private val appState: StateFlow<AppState>,
    private val mapUiState: StateFlow<MapUiState>,
) {
    init {
        logStateChangesWhenDebug()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun logStateChangesWhenDebug() {
        GlobalScope.launch {
            doWhen(debug) {
                val appLogs = doOnStateChangeLog(appState) {
                    SLog.v("AppState changed:  ⤵ \n\t${it.joinToString("\n\t")}")
                }
                val appUiLogs = doOnStateChangeLog(mapUiState) {
                    SLog.v("AppUiState changed:  ⤵ \n\t${it.joinToString("\n\t")}")
                }
                merge(appLogs, appUiLogs)
            }
        }
    }

    private fun <T : LoggedProperties<T>> doOnStateChangeLog(state: StateFlow<T>, action: (List<String>) -> Unit) =
        state.scan(state.value) { prev, curr ->
            stateChangeLog(prev, curr)?.let { action(it) }
            curr
        }

    private fun <S : LoggedProperties<S>> stateChangeLog(oldState: S, newState: S): List<String>? {
        val trackedProperties: List<KProperty1<S, Any?>> = oldState.getTrackedProperties()
        val changes = trackedProperties.mapNotNull { prop ->
            val old = prop.get(oldState)
            val new = prop.get(newState)
            if (old != new) {
                "${prop.name}: $old -> $new"
            } else null
        }
        return changes.ifEmpty { null }
    }
}

/** Implement if you want to track a state classes state changes via [AppStateChangeLogger] */
interface LoggedProperties<T> {
    fun getTrackedProperties(): List<KProperty1<T, Any?>>
}

val mapUiStateTrackedProperties = listOf(
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

val appStateTrackedProperties = listOf(
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