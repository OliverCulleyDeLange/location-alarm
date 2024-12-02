package uk.co.oliverdelange.location_alarm.screens

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import model.ui.AppViewModel
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.resources.StringProvider

class AppViewModel(stringProvider: StringProvider) : AppViewModel() {
    val toggleAlarmButtonText = state
        .map { it.alarmEnabled }
        .distinctUntilChanged()
        .map { enabled ->
            val resId = if (enabled) R.string.disable_alarm else R.string.enable_alarm
            stringProvider.getString(resId)
        }

}