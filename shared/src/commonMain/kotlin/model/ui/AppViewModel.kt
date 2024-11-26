package model.ui

import com.rickclephas.kmp.observableviewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import model.domain.AppState
import model.domain.Location
import model.domain.PermissionState

class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    fun onLocationPermissionResult(granted: Boolean) {
        println("location permission granted: $granted") // TODO Shared logging
        _state.update { current ->
            current.copy(
                locationPermissionState = if (granted) PermissionState.Granted else PermissionState.Denied
            )
        }
    }

    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    fun onLocationChange(locations: List<Location>) {
        println("Location updated $locations")
        locations.firstOrNull()?.let { firstLocation ->
            _state.update { it.copy(usersLocation = firstLocation) }
            if (!_state.value.mapInteracted) {
                _state.update {
                    it.copy(geoFenceLocation = firstLocation)
                }
            }
        } ?: println("Location update contains no location")
    }
}
