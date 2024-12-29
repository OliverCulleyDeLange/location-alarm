package uk.co.oliverdelange.locationalarm.model.ui.debug

import uk.co.oliverdelange.locationalarm.model.ui.UiState

data class DebugUiState(
    val logs: List<LogEntryUiModel> = emptyList(),
) : UiState
