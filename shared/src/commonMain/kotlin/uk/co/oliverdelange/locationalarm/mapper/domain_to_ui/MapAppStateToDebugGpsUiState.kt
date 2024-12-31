package uk.co.oliverdelange.locationalarm.mapper.domain_to_ui

import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.model.ui.debug.GpsUiModel
import uk.co.oliverdelange.locationalarm.model.ui.debug.GpsUiState

class MapAppStateToDebugGpsUiState {
    fun map(state: AppState): GpsUiState {
        return GpsUiState(
            gps = state.usersLocationHistory.map {
                GpsUiModel(it.date.toString())
            }
        )
    }
}