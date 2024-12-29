package uk.co.oliverdelange.locationalarm.mapper.domain_to_ui

import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET
import uk.co.oliverdelange.locationalarm.model.domain.DebugState
import uk.co.oliverdelange.locationalarm.model.domain.LogLevel
import uk.co.oliverdelange.locationalarm.model.ui.debug.DebugUiState
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogColor
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogEntryUiModel

class MapDebugStateToDebugUiState {
    fun map(state: DebugState): DebugUiState {
        return DebugUiState(
            logs = state.logs.map {
                LogEntryUiModel(
                    date = it.date.format(ISO_DATE_TIME_OFFSET),
                    message = it.message,
                    color = when (it.level) {
                        LogLevel.Error -> LogColor.Red
                        LogLevel.Warn -> LogColor.Orange
                        LogLevel.Info -> LogColor.Green
                        LogLevel.Debug -> LogColor.Blue
                        LogLevel.Verbose -> LogColor.Grey
                    }
                )
            }
        )
    }
}