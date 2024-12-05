package uk.co.oliverdelange.location_alarm.screens

import android.content.Context
import android.content.Intent
import com.rickclephas.kmp.observableviewmodel.launch
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

    init {
        viewModelScope.launch {
            // Start and stop the foreground service based on alarm enabled state
            state.map { it.alarmEnabled }.distinctUntilChanged().collect { enabled ->
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
    }
}