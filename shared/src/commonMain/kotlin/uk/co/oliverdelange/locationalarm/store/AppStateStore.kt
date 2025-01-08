package uk.co.oliverdelange.locationalarm.store

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.model.domain.LocationUpdate
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState
import uk.co.oliverdelange.locationalarm.model.domain.delayAlarmTriggering
import uk.co.oliverdelange.locationalarm.model.domain.denied
import uk.co.oliverdelange.locationalarm.model.domain.granted
import uk.co.oliverdelange.locationalarm.model.domain.shouldDelayAlarm
import uk.co.oliverdelange.locationalarm.navigation.Navigate
import uk.co.oliverdelange.locationalarm.navigation.Route
import uk.co.oliverdelange.locationalarm.provider.SystemTimeProvider
import uk.co.oliverdelange.locationalarm.provider.TimeProvider
import kotlin.math.roundToInt

open class AppStateStore(
    private val timeProvider: TimeProvider = SystemTimeProvider(),
) {
    private val _state = MutableStateFlow(AppState())

    @NativeCoroutinesState
    val state: StateFlow<AppState> = _state.asStateFlow()

    // TODO Learn more about these scopes - are they appropriate?
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun doNavigate(route: Navigate) {
        _state.update { platformDoNavigation(it, route) }
    }

    fun didNavigate(currentScreen: Route) {
        _state.update { it.copy(currentScreen = currentScreen, navigateTo = null) }
    }

    fun onTapAllowLocationPermissions() {
        _state.update { it.copy(shouldRequestLocationPermissions = true) }
    }

    fun onTapAllowNotificationPermissions() {
        _state.update { it.copy(shouldRequestNotificationPermissions = true) }
    }

    fun onRequestedLocationPermissions() {
        _state.update { it.copy(shouldRequestLocationPermissions = false) }
    }

    fun onRequestedNotificationPermissions() {
        _state.update { it.copy(shouldRequestNotificationPermissions = false) }
    }

    fun onLocationPermissionResult(state: PermissionState) {
        _state.update { current ->
            val navRoute = when {
                state.granted() -> {
                    val currentScreenIsLocationPermissionIssueScreen = current.currentScreen == Route.LocationPermissionDeniedScreen ||
                        current.currentScreen == Route.LocationPermissionRequiredScreen
                    if (currentScreenIsLocationPermissionIssueScreen) {
                        Route.MapScreen
                    } else current.currentScreen
                }

                state.denied() -> Route.LocationPermissionDeniedScreen
                else -> Route.LocationPermissionRequiredScreen
            }
            val navigate = Navigate(navRoute, current.currentScreen)
            val tmpState = current.copy(
                locationPermissionState = state,
                shouldRequestLocationPermissions = false,
            )
            if (current.currentScreen != navigate.route) {
                platformDoNavigation(tmpState, navigate)
            } else {
                tmpState
            }
        }
    }

    fun onLocationPermissionChecked() {
        _state.update { it.copy(shouldCheckLocationPermissions = false) }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        onNotificationPermissionResult(if (granted) PermissionState.Granted else PermissionState.Denied(false))
    }

    fun onNotificationPermissionResult(state: PermissionState) {
        _state.update { current ->
            current.copy(
                notificationPermissionState = state,
                shouldRequestNotificationPermissions = false,
            )
        }

        // If notification permissions are granted and the user is requesting the enable the alarm, we should honor this
        if (state == PermissionState.Granted && _state.value.userRequestedAlarmEnable) {
            onSetAlarm(true)
        }
    }

    fun onNotificationPermissionChecked() {
        _state.update { it.copy(shouldCheckNotificationPermissions = false) }
    }

    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    fun onLocationChange(locations: List<Location>) {
        locations.firstOrNull()?.let { firstLocation ->
            val now = timeProvider.now()
            _state.update {
                it.copy(
                    usersLocation = firstLocation,
                    usersLocationHistory = it.usersLocationHistory.plus(
                        LocationUpdate(now, firstLocation)
                    )
                )
            }
            if (!_state.value.mapInteracted) {
                _state.update {
                    it.copy(geoFenceLocation = firstLocation)
                }
            }
            recomputeDistancesAndTriggered()
        } ?: SLog.w("Location update contains no location")
    }

    /** Recompute distances to alarm, and triggered state
     * */
    private fun recomputeDistancesAndTriggered() {
        _state.update { state ->
            val distanceToGeofence = getDistanceToGeofence(state.usersLocation, state.geoFenceLocation)
            val distanceToGeofencePerimeter = getDistanceToGeofencePerimeter(
                state.usersLocation,
                state.geoFenceLocation,
                state.perimeterRadiusMeters
            )
            val triggered = state.alarmEnabled
                && !state.shouldDelayAlarm()
                && (distanceToGeofencePerimeter?.let { it <= 0 } ?: false)

            state.copy(
                distanceToGeofence = distanceToGeofence,
                distanceToGeofencePerimeter = distanceToGeofencePerimeter,
                alarmTriggered = triggered
            )
        }
    }

    fun onRadiusChanged(radius: Int) {
        val newRadius = radius.coerceAtLeast(10)
        _state.update { state ->
            state.copy(
                perimeterRadiusMeters = newRadius,
                distanceToGeofencePerimeter = getDistanceToGeofencePerimeter(state.usersLocation, state.geoFenceLocation, newRadius)
            )
        }
    }

    fun onMapTap(newGeofenceLocation: Location) {
        _state.update { state ->
            state.copy(
                mapInteracted = true,
                geoFenceLocation = newGeofenceLocation,
            )
        }
        recomputeDistancesAndTriggered()
    }

    fun onAppForegrounded() {
        _state.update { state ->
            state.copy(
                appInForeground = true,
                shouldListenForLocationUpdates = state.locationPermissionState.granted(),
                shouldCheckLocationPermissions = true,
                shouldCheckNotificationPermissions = true,
            )
        }
    }

    fun onAppBackgrounded() {
        _state.update { state ->
            state.copy(
                appInForeground = false,
                shouldListenForLocationUpdates = state.alarmEnabled,
            )
        }
    }

    fun onMapShown() {
        _state.update { state ->
            state.copy(shouldListenForLocationUpdates = true)
        }
    }

    fun onMapNotShown() {
        _state.update { state ->
            state.copy(shouldListenForLocationUpdates = state.alarmEnabled)
        }
    }

    fun onTapLocationIcon() {
        _state.update { state ->
            state.copy(usersLocationToFlyTo = state.usersLocation)
        }
    }

    fun onFinishFlyingToUsersLocation() {
        _state.update { state ->
            state.copy(usersLocationToFlyTo = null)
        }
    }

    fun onTapStopAlarm() {
        onSetAlarm(false)
    }

    /** Check notification permissions and enable if granted
     * Otherwise, request notification permissions
     * @param delay: Dev tool do delay alarm triggering by 5 seconds */
    fun onToggleAlarm() {
        when (_state.value.notificationPermissionState) {
            PermissionState.Granted -> {
                val alarmEnabled = !state.value.alarmEnabled
                onSetAlarm(alarmEnabled)
            }

            else -> {
                _state.update { state ->
                    state.copy(
                        userRequestedAlarmEnable = true,
                        shouldRequestNotificationPermissions = true,
                    )
                }
            }
        }
    }

    fun onSetAlarm(enabled: Boolean) {
        _state.update { state ->
            state.copy(
                alarmEnabled = enabled,
                alarmEnabledAt = if (enabled) timeProvider.now() else state.alarmEnabledAt,
                mapInteracted = true,
                userRequestedAlarmEnable = if (enabled) false else state.userRequestedAlarmEnable,
            )
        }
        recomputeDistancesAndTriggered()
        if (!enabled) delayAlarmTriggering = false
    }

    // Dev tool to allow enabling the alarm, but not allow triggering until a given time has passed
    fun onToggleAlarmWithDelay() {
        delayAlarmTriggering = true
        onToggleAlarm()
        coroutineScope.launch {
            delay(5000)
            recomputeDistancesAndTriggered()
        }
    }

    fun setDebug(debug: Boolean) {
        SLog.w("DEBUG MODE: $debug")
        _state.update { it.copy(debug = debug) }
    }

    private fun getDistanceToGeofence(
        usersLocation: Location?,
        geofenceLocation: Location?
    ): Int? {
        return if (usersLocation != null && geofenceLocation != null) {
            geofenceLocation.distanceTo(usersLocation).roundToInt()
        } else null
    }

    private fun getDistanceToGeofencePerimeter(
        usersLocation: Location?,
        geofenceLocation: Location?,
        perimeterRadiusMeters: Int
    ): Int? {
        return getDistanceToGeofence(usersLocation, geofenceLocation)?.minus(perimeterRadiusMeters)
    }
}

/** IOS Doesn't navigate, it just responds to the currentScreen changing */
expect fun AppStateStore.platformDoNavigation(
    appState: AppState,
    route: Navigate
): AppState