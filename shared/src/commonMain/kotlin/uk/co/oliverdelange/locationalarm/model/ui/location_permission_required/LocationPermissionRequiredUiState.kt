package uk.co.oliverdelange.locationalarm.model.ui.location_permission_required

import uk.co.oliverdelange.locationalarm.logging.LoggedProperties
import uk.co.oliverdelange.locationalarm.logging.locationPermissionRequiredTrackedPropertied
import uk.co.oliverdelange.locationalarm.model.ui.UiState

data class LocationPermissionRequiredUiState(
    val shouldShowContent: Boolean = false
) : UiState, LoggedProperties<LocationPermissionRequiredUiState> {
    override fun getTrackedProperties() = locationPermissionRequiredTrackedPropertied
}

