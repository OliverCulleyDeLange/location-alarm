package model.domain

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

open class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(viewModelScope, MapFeatureState())

    @NativeCoroutinesState
    val state: StateFlow<MapFeatureState> = _state.asStateFlow()

    fun onTapAllowLocationPermissions() {
        Logger.d { "onTapAllowLocationPermissions" }
        _state.update { it.copy(shouldRequestLocationPermissions = true) }
    }

    fun onRequestedLocationPermissions() {
        _state.update { it.copy(shouldRequestLocationPermissions = false) }
    }

    fun onLocationPermissionResult(state: PermissionState) {
        Logger.d { "Location permission updated: $state" }
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
        Logger.d { "Notification permission updated: $state" }
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
        Logger.v { "Location updated $locations" }
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
            _state.update { state ->
                state.copy(
                    distanceToGeofence = getDistanceToGeofence(state.usersLocation, state.geoFenceLocation),
                    distanceToGeofencePerimeter = getDistanceToGeofencePerimeter(
                        state.usersLocation,
                        state.geoFenceLocation,
                        state.perimeterRadiusMeters
                    )
                )
            }
        } ?: Logger.w { "Location update contains no location" }
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
        Logger.d("Map tapped $newGeofenceLocation")
        _state.update { state ->
            state.copy(
                mapInteracted = true,
                geoFenceLocation = newGeofenceLocation,
                distanceToGeofence = getDistanceToGeofence(state.usersLocation, newGeofenceLocation),
                distanceToGeofencePerimeter = getDistanceToGeofencePerimeter(state.usersLocation, newGeofenceLocation, state.perimeterRadiusMeters)
            )
        }
    }

    fun onTapLocationIcon() {
        _state.update { state ->
            state.copy(usersLocationToFlyTo = state.usersLocation)
        }
    }

    fun onTapStopAlarm() {
        onSetAlarm(false)
    }

    fun onFinishFlyingToUsersLocation() {
        _state.update { state ->
            state.copy(usersLocationToFlyTo = null)
        }
    }

    /** Check notification permissions and enable if granted
     * Otherwise, request notification permissions */
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
                mapInteracted = true,
                userRequestedAlarmEnable = if (enabled) false else state.userRequestedAlarmEnable
            )
        }
    }

    // Dev tool to allow enabling the alarm, but not allow triggering until a given number of location updates have come through
    fun onToggleAlarmWithDelay(locationUpdates: Int) {
        onToggleAlarm()
        _state.update { state ->
            state.copy(
                alarmTriggerDelayLocationHistorySize = state.usersLocationHistory.size + locationUpdates,
            )
        }
    }

    private fun getDistanceToGeofence(usersLocation: Location?, geofenceLocation: Location?): Int? {
        return if (usersLocation != null && geofenceLocation != null) {
            geofenceLocation.distanceTo(usersLocation).roundToInt()
        } else null
    }

    private fun getDistanceToGeofencePerimeter(usersLocation: Location?, geofenceLocation: Location?, perimeterRadiusMeters: Int): Int? {
        return getDistanceToGeofence(usersLocation, geofenceLocation)?.minus(perimeterRadiusMeters)
    }
}
