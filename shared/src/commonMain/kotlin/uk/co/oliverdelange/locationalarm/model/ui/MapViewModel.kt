package uk.co.oliverdelange.locationalarm.model.ui

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToMapUiState
import uk.co.oliverdelange.locationalarm.model.domain.AppStateStore
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.FinishedFLyingToUsersLocation
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.LocationChanged
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.LocationPermissionResult
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.MapNotShown
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.MapShown
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.NotificationPermissionResult
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.RequestedLocationPermission
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.DraggedRadiusControl
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedAllowLocationPermissions
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedLocationIcon
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedMap
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedStopAlarm
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.ToggledAlarm

open class MapViewModel(
    private val appStateStore: AppStateStore,
    private val uiStateMapper: MapAppStateToMapUiState,
) : ViewModel(), MapViewModelInterface {

    @NativeCoroutinesState
    val state: StateFlow<MapUiState> = appStateStore.state
        .map(uiStateMapper::map)
        .stateIn(viewModelScope, SharingStarted.Lazily, MapUiState())

    init {
        //TODO Only if debug (pass in from client?)
        viewModelScope.launch {
            state.map { it.toDebugString() }.distinctUntilChanged().collect {
                // This only logs when the debug string changes, so if you wanna track something, add it.
                Logger.w("NEW MAP UI STATE: $it")
            }
        }
    }

    override fun onEvent(uiEvent: UiEvents) {
        when (uiEvent) {
            // User Events
            is TappedAllowLocationPermissions -> appStateStore.onTapAllowLocationPermissions()
            is DraggedRadiusControl -> appStateStore.onRadiusChanged(uiEvent.radius)
            is TappedMap -> appStateStore.onMapTap(uiEvent.location)
            is TappedLocationIcon -> appStateStore.onTapLocationIcon()
            is TappedStopAlarm -> appStateStore.onTapStopAlarm()
            is ToggledAlarm -> appStateStore.onToggleAlarm()
            is UserEvent.ToggledAlarmWithDelay -> appStateStore.onToggleAlarmWithDelay()
            // Ui Results
            is LocationChanged -> appStateStore.onLocationChange(uiEvent.location)
            is LocationPermissionResult -> appStateStore.onLocationPermissionResult(uiEvent.state)
            is NotificationPermissionResult -> appStateStore.onNotificationPermissionResult(uiEvent.state)
            is RequestedLocationPermission -> appStateStore.onRequestedLocationPermissions()
            is FinishedFLyingToUsersLocation -> appStateStore.onFinishFlyingToUsersLocation()
            is MapShown -> appStateStore.onMapShown()
            is MapNotShown -> appStateStore.onMapNotShown()
            else -> {
                Logger.v("Unhandled UI event: $uiEvent")
            }
        }
    }
}
