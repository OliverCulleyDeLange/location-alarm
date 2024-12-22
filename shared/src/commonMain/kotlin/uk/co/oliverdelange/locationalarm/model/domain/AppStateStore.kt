package uk.co.oliverdelange.locationalarm.model.domain

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.update
import uk.co.oliverdelange.locationalarm.logging.stateChangeLog
import uk.co.oliverdelange.locationalarm.provider.SystemTimeProvider
import uk.co.oliverdelange.locationalarm.provider.TimeProvider
import kotlin.math.roundToInt

open class AppStateStore(
    private val timeProvider: TimeProvider = SystemTimeProvider()
) : ViewModel() {
    private val _state = MutableStateFlow(viewModelScope, AppState())

    @NativeCoroutinesState
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            state.scan(state.value) { prev, curr ->
                stateChangeLog(prev, curr)?.let {
                    Logger.w("AppState changed:  ⤵ \n\t${it.joinToString("\n\t")}")
                }
                curr
            }.collect()
        }
    }

    fun onTapAllowLocationPermissions() {
        _state.update { it.copy(shouldRequestLocationPermissions = true) }
    }

    fun onRequestedLocationPermissions() {
        _state.update { it.copy(shouldRequestLocationPermissions = false) }
    }

    fun onLocationPermissionResult(state: PermissionState) {
        _state.update { current ->
            current.copy(
                locationPermissionState = state,
                shouldRequestLocationPermissions = false,
            )
        }
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

    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    fun onLocationChange(locations: List<Location>) {
        locations.firstOrNull()?.let { firstLocation ->
            _state.update {
                it.copy(
                    usersLocation = firstLocation,
                    usersLocationHistory = it.usersLocationHistory.plus(firstLocation)
                )
            }
            if (!_state.value.mapInteracted) {
                _state.update {
                    it.copy(geoFenceLocation = firstLocation)
                }
            }
            recomputeDistancesAndTriggered()
        } ?: Logger.w { "Location update contains no location" }
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
        viewModelScope.launch {
            delay(5000)
            recomputeDistancesAndTriggered()
        }
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
