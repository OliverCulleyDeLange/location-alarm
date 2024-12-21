package uk.co.oliverdelange.locationalarm.model.ui

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToMapUiState
import uk.co.oliverdelange.locationalarm.model.domain.AppStateStore
import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState

open class MapViewModel(
    private val appStateStore: AppStateStore,
    private val uiStateMapper: MapAppStateToMapUiState,
) : ViewModel() {

    @NativeCoroutinesState
    val state: StateFlow<MapUiState> = appStateStore.state
        .map(uiStateMapper::map)
        .stateIn(viewModelScope, SharingStarted.Lazily, MapUiState())

    init {
        viewModelScope.launch {
            state.map { it.toDebugString() }.distinctUntilChanged().collect {
                // This only logs when the debug string changes, so if you wanna track something, add it.
                Logger.w("NEW MAP UI STATE: $it")
            }
        }
    }

    fun onTapAllowLocationPermissions() {
        appStateStore.onTapAllowLocationPermissions()
    }

    fun onRequestedLocationPermissions() {
        appStateStore.onRequestedLocationPermissions()
    }

    fun onLocationPermissionResult(state: PermissionState) {
        appStateStore.onLocationPermissionResult(state)
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        appStateStore.onNotificationPermissionResult(granted)
    }

    fun onNotificationPermissionResult(state: PermissionState) {
        appStateStore.onNotificationPermissionResult(state)
    }

    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    fun onLocationChange(locations: List<Location>) {
        appStateStore.onLocationChange(locations)
    }

    fun onRadiusChanged(radius: Int) {
        appStateStore.onRadiusChanged(radius)
    }

    fun onMapTap(newGeofenceLocation: Location) {
        appStateStore.onMapTap(newGeofenceLocation)
    }

    fun onMapShown() {
        appStateStore.onMapShown()
    }

    fun onMapNotShown() {
        appStateStore.onMapNotShown()
    }

    fun onTapLocationIcon() {
        appStateStore.onTapLocationIcon()
    }

    fun onFinishFlyingToUsersLocation() {
        appStateStore.onFinishFlyingToUsersLocation()
    }

    fun onTapStopAlarm() {
        appStateStore.onTapStopAlarm()
    }

    /** Check notification permissions and enable if granted
     * Otherwise, request notification permissions
     * @param delay: Dev tool do delay alarm triggering by 5 seconds */
    fun onToggleAlarm() {
        appStateStore.onToggleAlarm()
    }

    fun onSetAlarm(enabled: Boolean) {
        appStateStore.onSetAlarm(enabled)
    }

    // Dev tool to allow enabling the alarm, but not allow triggering until a given time has passed
    fun onToggleAlarmWithDelay() {
        appStateStore.onToggleAlarmWithDelay()
    }

}
