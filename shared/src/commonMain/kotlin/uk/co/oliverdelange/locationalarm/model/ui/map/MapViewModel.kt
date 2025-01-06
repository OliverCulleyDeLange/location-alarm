package uk.co.oliverdelange.locationalarm.model.ui.map

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToMapUiState
import uk.co.oliverdelange.locationalarm.model.ui.UiEvents
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.FinishedFLyingToUsersLocation
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.LocationChanged
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.MapNotShown
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.MapShown
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.NotificationPermissionResult
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.RequestedLocationPermission
import uk.co.oliverdelange.locationalarm.model.ui.UiResult.RequestedNotificationPermission
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.DraggedRadiusControl
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedLocationIcon
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedMap
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedStopAlarm
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.ToggledAlarm
import uk.co.oliverdelange.locationalarm.model.ui.ViewModelInterface
import uk.co.oliverdelange.locationalarm.navigation.Navigate
import uk.co.oliverdelange.locationalarm.store.AppStateStore

open class MapViewModel(
    private val appStateStore: AppStateStore,
    private val uiStateMapper: MapAppStateToMapUiState,
) : ViewModel(), ViewModelInterface {

    @NativeCoroutinesState
    val state: StateFlow<MapUiState> = appStateStore.state
        .map(uiStateMapper::map)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MapUiState())

    init {
        SLog.d("MapViewModel init")
    }

    override fun onEvent(uiEvent: UiEvents) {
        when (uiEvent) {
            // Navigation
            is Navigate -> appStateStore.doNavigate(uiEvent)
            // User Events
            is DraggedRadiusControl -> appStateStore.onRadiusChanged(uiEvent.radius)
            is TappedMap -> appStateStore.onMapTap(uiEvent.location)
            is TappedLocationIcon -> appStateStore.onTapLocationIcon()
            is TappedStopAlarm -> appStateStore.onTapStopAlarm()
            is ToggledAlarm -> appStateStore.onToggleAlarm()
            is UserEvent.ToggledAlarmWithDelay -> appStateStore.onToggleAlarmWithDelay()
            is UserEvent.TappedAllowNotificationPermissions -> appStateStore.onTapAllowNotificationPermissions()
            // Ui Results
            is LocationChanged -> appStateStore.onLocationChange(uiEvent.location)
            is NotificationPermissionResult -> appStateStore.onNotificationPermissionResult(uiEvent.state)
            is RequestedLocationPermission -> appStateStore.onRequestedLocationPermissions()
            is RequestedNotificationPermission -> appStateStore.onRequestedNotificationPermissions()
            is FinishedFLyingToUsersLocation -> appStateStore.onFinishFlyingToUsersLocation()
            is MapShown -> appStateStore.onMapShown()
            is MapNotShown -> appStateStore.onMapNotShown()
            else -> {
                SLog.v("Unhandled UI event: $uiEvent")
            }
        }
    }

    override fun onCleared() {
        SLog.d("onCleared MapUiViewModel")
        super.onCleared()
    }
}
