package uk.co.oliverdelange.location_alarm.screens

import android.content.Context
import android.content.Intent
import com.rickclephas.kmp.observableviewmodel.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import model.domain.AppViewModel
import model.domain.MapFeatureState
import model.ui.MapUiState
import model.ui.UiViewModel
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.resources.StringProvider
import uk.co.oliverdelange.location_alarm.service.LocationAlarmService

/** App side extension of the shared view model
 * Handles app side ui model mapping
 * */
class MapUiViewModel(val context: Context, val stringProvider: StringProvider) : AppViewModel(), UiViewModel<MapFeatureState, MapUiState> {
    override val uiState = state
        .map(::mapUiState)

    override fun mapUiState(state: MapFeatureState): MapUiState {
        val toggleAlarmButtonTextResId = if (state.alarmEnabled) R.string.disable_alarm else R.string.enable_alarm
        return MapUiState(
            toggleAlarmButtonText = stringProvider.getString(toggleAlarmButtonTextResId)
        )
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