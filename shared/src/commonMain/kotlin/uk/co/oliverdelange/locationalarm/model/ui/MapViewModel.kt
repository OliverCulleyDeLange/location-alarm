package uk.co.oliverdelange.locationalarm.model.ui

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
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
) : MapViewModelInterface() {

    @NativeCoroutinesState
    override val state: StateFlow<MapUiState> = appStateStore.state
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

    override fun onTapAllowLocationPermissions() {
        appStateStore.onTapAllowLocationPermissions()
    }

    override fun onRequestedLocationPermissions() {
        appStateStore.onRequestedLocationPermissions()
    }

    override fun onLocationPermissionResult(state: PermissionState) {
        appStateStore.onLocationPermissionResult(state)
    }

    override fun onNotificationPermissionResult(granted: Boolean) {
        appStateStore.onNotificationPermissionResult(granted)
    }

    override fun onNotificationPermissionResult(state: PermissionState) {
        appStateStore.onNotificationPermissionResult(state)
    }

    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    override fun onLocationChange(locations: List<Location>) {
        appStateStore.onLocationChange(locations)
    }

    override fun onRadiusChanged(radius: Int) {
        appStateStore.onRadiusChanged(radius)
    }

    override fun onMapTap(newGeofenceLocation: Location) {
        appStateStore.onMapTap(newGeofenceLocation)
    }

    override fun onMapShown() {
        appStateStore.onMapShown()
    }

    override fun onMapNotShown() {
        appStateStore.onMapNotShown()
    }

    override fun onTapLocationIcon() {
        appStateStore.onTapLocationIcon()
    }

    override fun onFinishFlyingToUsersLocation() {
        appStateStore.onFinishFlyingToUsersLocation()
    }

    override fun onTapStopAlarm() {
        appStateStore.onTapStopAlarm()
    }

    /** Check notification permissions and enable if granted
     * Otherwise, request notification permissions
     * @param delay: Dev tool do delay alarm triggering by 5 seconds */
    override fun onToggleAlarm() {
        appStateStore.onToggleAlarm()
    }

    override fun onSetAlarm(enabled: Boolean) {
        appStateStore.onSetAlarm(enabled)
    }

    // Dev tool to allow enabling the alarm, but not allow triggering until a given time has passed
    override fun onToggleAlarmWithDelay() {
        appStateStore.onToggleAlarmWithDelay()
    }

}
