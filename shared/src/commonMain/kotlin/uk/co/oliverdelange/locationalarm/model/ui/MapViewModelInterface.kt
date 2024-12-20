package uk.co.oliverdelange.locationalarm.model.ui

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState

/** To allow faking viewmodels for UI Previews */
abstract class MapViewModelInterface : ViewModel() {
    @NativeCoroutinesState
    abstract val state: StateFlow<MapUiState>

    abstract fun onTapAllowLocationPermissions()
    abstract fun onRequestedLocationPermissions()
    abstract fun onLocationPermissionResult(state: PermissionState)
    abstract fun onNotificationPermissionResult(granted: Boolean)
    abstract fun onNotificationPermissionResult(state: PermissionState)

    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    abstract fun onLocationChange(locations: List<Location>)
    abstract fun onRadiusChanged(radius: Int)
    abstract fun onMapTap(newGeofenceLocation: Location)
    abstract fun onMapShown()
    abstract fun onMapNotShown()
    abstract fun onTapLocationIcon()
    abstract fun onFinishFlyingToUsersLocation()
    abstract fun onTapStopAlarm()

    /** Check notification permissions and enable if granted
     * Otherwise, request notification permissions
     * @param delay: Dev tool do delay alarm triggering by 5 seconds */
    abstract fun onToggleAlarm()
    abstract fun onSetAlarm(enabled: Boolean)

    // Dev tool to allow enabling the alarm, but not allow triggering until a given time has passed
    abstract fun onToggleAlarmWithDelay()
}