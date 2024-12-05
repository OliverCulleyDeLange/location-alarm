package uk.co.oliverdelange.location_alarm.screens

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import model.ui.AppViewModel
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.resources.StringProvider
import uk.co.oliverdelange.location_alarm.service.LocationAlarmService

class AppViewModel(val context: Context, stringProvider: StringProvider) : AppViewModel() {
    val toggleAlarmButtonText = state
        .map { it.alarmEnabled }
        .distinctUntilChanged()
        .map { enabled ->
            val resId = if (enabled) R.string.disable_alarm else R.string.enable_alarm
            stringProvider.getString(resId)
        }

    override fun onSetAlarm(enabled: Boolean) {
        super.onSetAlarm(enabled)
        val intent = Intent(context, LocationAlarmService::class.java)
        if (enabled) {
            Timber.i("Starting LocationAlarmService")
            context.startForegroundService(intent)
        } else {
            Timber.i("Stopping LocationAlarmService")
            context.stopService(intent)
        }
    }
}