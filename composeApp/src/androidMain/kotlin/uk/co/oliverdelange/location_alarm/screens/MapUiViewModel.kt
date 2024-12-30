package uk.co.oliverdelange.location_alarm.screens

import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToMapUiState
import uk.co.oliverdelange.locationalarm.model.ui.map.MapViewModel
import uk.co.oliverdelange.locationalarm.store.AppStateStore

/** App side extension of the shared view model */
class MapUiViewModel(
    appStateStore: AppStateStore,
    uiStateMapper: MapAppStateToMapUiState,
) : MapViewModel(appStateStore, uiStateMapper) {
    override fun onCleared() {
        SLog.d("onCleared MapUiViewModel")
        super.onCleared()
    }
}