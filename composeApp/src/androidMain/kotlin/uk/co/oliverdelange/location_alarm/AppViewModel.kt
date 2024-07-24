package uk.co.oliverdelange.location_alarm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AppState(
    val locationPermissionState: PermissionState = PermissionState.Unknown
)

class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    fun onPermissionResult(granted: Boolean) {
        _state.update { current ->
            current.copy(
                locationPermissionState = if (granted) PermissionState.Granted else PermissionState.Denied
            )
        }
    }
}
