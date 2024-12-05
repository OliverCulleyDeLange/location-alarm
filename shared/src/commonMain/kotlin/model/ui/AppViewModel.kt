package model.ui

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import model.domain.AppState
import model.domain.Location
import model.domain.PermissionState
import kotlin.math.roundToInt

open class AppViewModel : ViewModel() {
    protected val _state = MutableStateFlow(viewModelScope, AppState())

    @NativeCoroutinesState
    val state: StateFlow<AppState> = _state.asStateFlow()

    fun onLocationPermissionResult(granted: Boolean) {
        Logger.d { "Location permission granted: $granted" }
        _state.update { current ->
            current.copy(
                locationPermissionState = if (granted) PermissionState.Granted else PermissionState.Denied
            )
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        Logger.d { "Notification permission granted: $granted" }
        _state.update { current ->
            current.copy(
                notificationPermissionState = if (granted) PermissionState.Granted else PermissionState.Denied
            )
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

    private fun getDistanceToGeofence(usersLocation: Location?, geofenceLocation: Location?): Int? {
        return if (usersLocation != null && geofenceLocation != null) {
            geofenceLocation.distanceTo(usersLocation).roundToInt()
        } else null
    }

    private fun getDistanceToGeofencePerimeter(usersLocation: Location?, geofenceLocation: Location?, perimeterRadiusMeters: Int): Int? {
        return getDistanceToGeofence(usersLocation, geofenceLocation)?.minus(perimeterRadiusMeters)
    }

    fun onToggleAlarm() {
        val alarmEnabled = !state.value.alarmEnabled
        onSetAlarm(alarmEnabled)
    }

    open fun onSetAlarm(enabled: Boolean) {
        _state.update { state ->
            state.copy(
                alarmEnabled = enabled,
                mapInteracted = true,
            )
        }
    }
}
