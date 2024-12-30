package uk.co.oliverdelange.locationalarm.mapper.domain_to_ui

import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.model.domain.unchecked
import uk.co.oliverdelange.locationalarm.model.ui.location_permission_required.LocationPermissionRequiredUiState

class MapAppStateToLocationPermissionRequiredUiState {
    fun map(state: AppState): LocationPermissionRequiredUiState {
        return LocationPermissionRequiredUiState(
            shouldShowContent = !state.locationPermissionState.unchecked()
        )
    }
}