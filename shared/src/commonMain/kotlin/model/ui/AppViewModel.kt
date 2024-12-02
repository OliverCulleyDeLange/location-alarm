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

class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(viewModelScope, AppState())

    @NativeCoroutinesState
    val state: StateFlow<AppState> = _state.asStateFlow()

    fun onLocationPermissionResult(granted: Boolean) {
        Logger.d { "location permission granted: $granted" }
        _state.update { current ->
            current.copy(
                locationPermissionState = if (granted) PermissionState.Granted else PermissionState.Denied
            )
        }
    }

    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    fun onLocationChange(locations: List<Location>) {
        Logger.d { "Location updated $locations" }
        locations.firstOrNull()?.let { firstLocation ->
            _state.update { it.copy(usersLocation = firstLocation) }
            if (!_state.value.mapInteracted) {
                _state.update {
                    it.copy(geoFenceLocation = firstLocation)
                }
            }
        } ?: Logger.w { "Location update contains no location" }
    }

    fun onRadiusChanged(radius: Int) {
        _state.update { it.copy(perimeterRadiusMeters = radius) }
    }

    fun onMapTap(location: Location) {
        Logger.d("Map tapped $location")
        _state.update {
            it.copy(
                mapInteracted = true,
                geoFenceLocation = location
            )
        }
    }
}
