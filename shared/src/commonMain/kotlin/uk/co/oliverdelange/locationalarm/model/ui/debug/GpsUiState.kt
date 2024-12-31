package uk.co.oliverdelange.locationalarm.model.ui.debug

import uk.co.oliverdelange.locationalarm.model.ui.UiState

data class GpsUiState(
    val gps: List<GpsUiModel> = emptyList(),
) : UiState {
    // For swift, to allow no args constructor
    constructor() : this(emptyList())
}
