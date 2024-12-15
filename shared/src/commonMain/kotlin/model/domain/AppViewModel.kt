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

    fun onLocationPermissionResult(granted: Boolean) {
        onLocationPermissionResult(if (granted) PermissionState.Granted else PermissionState.Denied)
    }

    fun onLocationPermissionResult(state: PermissionState) {
        Logger.d { "Location permission state: $state" }
        _state.update { current ->
            current.copy(locationPermissionState = state)
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        Logger.d { "Notification permission granted: $granted" }
        _state.update { current ->
            current.copy(
                notificationPermissionState = if (granted) PermissionState.Granted else PermissionState.Denied
            )
        }
        // If notification permissions are granted and the user is requesting the enable the alarm, we should honor this
        if (granted && _state.value.userRequestedAlarmEnable) {
            onSetAlarm(true)
        }
    }

    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    fun onLocationChange(locations: List<Location>) {
        Logger.v { "Location updated $locations" }
        locations.firstOrNull()?.let { firstLocation ->
            _state.update { it.copy(usersLocation = firstLocation) }
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
                _state.update { state -> state.copy(userRequestedAlarmEnable = true) }
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

    private fun getDistanceToGeofence(usersLocation: Location?, geofenceLocation: Location?): Int? {
        return if (usersLocation != null && geofenceLocation != null) {
            geofenceLocation.distanceTo(usersLocation).roundToInt()
        } else null
    }

    private fun getDistanceToGeofencePerimeter(usersLocation: Location?, geofenceLocation: Location?, perimeterRadiusMeters: Int): Int? {
        return getDistanceToGeofence(usersLocation, geofenceLocation)?.minus(perimeterRadiusMeters)
    }
}
