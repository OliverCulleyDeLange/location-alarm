package uk.co.oliverdelange.locationalarm.model.ui.location_permission_required

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToLocationPermissionRequiredUiState
import uk.co.oliverdelange.locationalarm.model.ui.UiEvents
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent.TappedAllowLocationPermissions
import uk.co.oliverdelange.locationalarm.model.ui.ViewModelInterface
import uk.co.oliverdelange.locationalarm.store.AppStateStore

open class LocationPermissionRequiredViewModel(
    private val appStateStore: AppStateStore,
    private val uiStateMapper: MapAppStateToLocationPermissionRequiredUiState = MapAppStateToLocationPermissionRequiredUiState(),
) : ViewModel(), ViewModelInterface {

    @NativeCoroutinesState
    val state: StateFlow<LocationPermissionRequiredUiState> = appStateStore.state
        .map(uiStateMapper::map)
        .stateIn(viewModelScope, SharingStarted.Lazily, LocationPermissionRequiredUiState())

    init {
        SLog.d("LocationPermissionRequiredViewModel init")
    }

    override fun onEvent(uiEvent: UiEvents) {
        when (uiEvent) {
            is TappedAllowLocationPermissions -> appStateStore.onTapAllowLocationPermissions()
            else -> {
                SLog.v("Unhandled UI event: $uiEvent")
            }
        }
    }
}
