package uk.co.oliverdelange.location_alarm.screens

import uk.co.oliverdelange.locationalarm.logging.Log
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToMapUiState
import uk.co.oliverdelange.locationalarm.model.domain.AppStateStore
import uk.co.oliverdelange.locationalarm.model.ui.MapViewModel

/** App side extension of the shared view model
 * Handles app side ui model mapping
 * */
class MapUiViewModel(
    appStateStore: AppStateStore,
    uiStateMapper: MapAppStateToMapUiState,
) : MapViewModel(appStateStore, uiStateMapper) {
    override fun onCleared() {
        Log.d("onCleared MapUiViewModel")
        super.onCleared()
    }
}