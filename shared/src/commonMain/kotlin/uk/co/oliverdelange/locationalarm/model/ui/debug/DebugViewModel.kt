package uk.co.oliverdelange.locationalarm.model.ui.debug

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToDebugGpsUiState
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapDebugStateToDebugLogUiState
import uk.co.oliverdelange.locationalarm.model.ui.UiEvents
import uk.co.oliverdelange.locationalarm.model.ui.ViewModelInterface
import uk.co.oliverdelange.locationalarm.store.AppStateStore
import uk.co.oliverdelange.locationalarm.store.DebugStateStore

open class DebugViewModel(
    private val debugStateStore: DebugStateStore,
    private val appStateStore: AppStateStore,
    private val logStateMapper: MapDebugStateToDebugLogUiState,
    private val gpsStateMapper: MapAppStateToDebugGpsUiState,
) : ViewModel(), ViewModelInterface {

    init {
        SLog.d("DebugViewModel init")
    }

    @NativeCoroutinesState
    val logUiState: StateFlow<LogUiState> = debugStateStore.state
        .map(logStateMapper::map)
        .stateIn(viewModelScope, SharingStarted.Eagerly, LogUiState())

    @NativeCoroutinesState
    val gpsUiState: StateFlow<GpsUiState> = appStateStore.state
        .map(gpsStateMapper::map)
        .stateIn(viewModelScope, SharingStarted.Eagerly, GpsUiState())

    override fun onEvent(uiEvent: UiEvents) {
        when (uiEvent) {
            else -> {
                SLog.v("Unhandled UI event: $uiEvent")
            }
        }
    }
}
