package uk.co.oliverdelange.locationalarm.model.ui.debug

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapDebugStateToDebugUiState
import uk.co.oliverdelange.locationalarm.model.ui.MapViewModelInterface
import uk.co.oliverdelange.locationalarm.model.ui.UiEvents
import uk.co.oliverdelange.locationalarm.store.DebugStateStore

class DebugViewModel(
    debugStateStore: DebugStateStore,
    private val uiStateMapper: MapDebugStateToDebugUiState,
) : ViewModel(), MapViewModelInterface {

    init {
        SLog.d("DebugViewModel init")
    }

    @NativeCoroutinesState
    val state: StateFlow<DebugUiState> = debugStateStore.state
        .map(uiStateMapper::map)
        //FIXME This causes
        .stateIn(viewModelScope, SharingStarted.Eagerly, DebugUiState())

    override fun onEvent(uiEvent: UiEvents) {
        when (uiEvent) {
            else -> {
                SLog.v("Unhandled UI event: $uiEvent")
            }
        }
    }
}
