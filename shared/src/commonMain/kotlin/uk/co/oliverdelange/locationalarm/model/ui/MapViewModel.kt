package uk.co.oliverdelange.locationalarm.model.ui

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import uk.co.oliverdelange.locationalarm.helper.doWhen
import uk.co.oliverdelange.locationalarm.logging.stateChangeLog
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToMapUiState
import uk.co.oliverdelange.locationalarm.model.domain.AppStateStore
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.FinishedFLyingToUsersLocation
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.LocationChanged
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.LocationPermissionResult
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.MapNotShown
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.MapShown
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.NotificationPermissionResult
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.RequestedLocationPermission
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.RequestedNotificationPermission
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.DraggedRadiusControl
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedAllowLocationPermissions
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedLocationIcon
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedMap
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedStopAlarm
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.ToggledAlarm

@OptIn(ExperimentalCoroutinesApi::class)
open class MapViewModel(
    private val appStateStore: AppStateStore,
    private val uiStateMapper: MapAppStateToMapUiState,
) : ViewModel(), MapViewModelInterface {

    @NativeCoroutinesState
    val state: StateFlow<MapUiState> = appStateStore.state
        .map(uiStateMapper::map)
        .stateIn(viewModelScope, SharingStarted.Lazily, MapUiState())

    init {
        viewModelScope.launch {
            doWhen(appStateStore.state.map { it.debug }) {
                state.scan(state.value) { prev, curr ->
                    stateChangeLog(prev, curr)?.let {
                        Logger.w("MapUiState changed: ⤵ \n\t${it.joinToString("\n\t")}")
                    }
                    curr
                }
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
            is UserEvent.OpenedDeepLinkStopAlarm -> appStateStore.onSetAlarm(false)
            // Ui Results
            is LocationChanged -> appStateStore.onLocationChange(uiEvent.location)
            is LocationPermissionResult -> appStateStore.onLocationPermissionResult(uiEvent.state)
            is NotificationPermissionResult -> appStateStore.onNotificationPermissionResult(uiEvent.state)
            is RequestedLocationPermission -> appStateStore.onRequestedLocationPermissions()
            is RequestedNotificationPermission -> appStateStore.onRequestedNotificationPermissions()
            is FinishedFLyingToUsersLocation -> appStateStore.onFinishFlyingToUsersLocation()
            is MapShown -> appStateStore.onMapShown()
            is MapNotShown -> appStateStore.onMapNotShown()
            else -> {
                Logger.v("Unhandled UI event: $uiEvent")
            }
        }
    }
}
