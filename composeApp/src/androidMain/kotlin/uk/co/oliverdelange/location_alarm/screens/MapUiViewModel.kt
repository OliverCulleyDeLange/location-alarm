package uk.co.oliverdelange.location_alarm.screens

import kotlinx.coroutines.flow.map
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.resources.StringProvider
import uk.co.oliverdelange.locationalarm.model.domain.AppViewModel
import uk.co.oliverdelange.locationalarm.model.domain.MapFeatureState
import uk.co.oliverdelange.locationalarm.model.ui.MapUiState
import uk.co.oliverdelange.locationalarm.model.ui.UiViewModel

/** App side extension of the shared view model
 * Handles app side ui model mapping
 * */
class MapUiViewModel(val stringProvider: StringProvider) : AppViewModel(), UiViewModel<MapFeatureState, MapUiState> {
    override val uiState = state
        .map(::mapUiState)

    override fun mapUiState(state: MapFeatureState): MapUiState {
        val toggleAlarmButtonTextResId = if (state.alarmEnabled) R.string.disable_alarm else R.string.enable_alarm
        return MapUiState(
            toggleAlarmButtonText = stringProvider.getString(toggleAlarmButtonTextResId)
        )
    }

    override fun onCleared() {
        Timber.d("onCleared MapUiViewModel")
        super.onCleared()
    }
}