package uk.co.oliverdelange.location_alarm.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.screens.map.MapScreen
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme

@Composable
@Preview
fun App(viewmodel: AppViewModel = viewModel()) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val alarmButtonText by viewmodel.toggleAlarmButtonText.collectAsStateWithLifecycle("")

    AppTheme {
        Box {
            MapScreen(
                state,
                alarmButtonText,
                onLocationUpdate = { locations -> viewmodel.onLocationChange(locations) },
                onMapTap = { location -> viewmodel.onMapTap(location) },
                onToggleAlarm = { viewmodel.onToggleAlarm() },
                onRadiusChange = { radius -> viewmodel.onRadiusChanged(radius) }
            )
            AlarmAlert(state.alarmTriggered) {
                viewmodel.onSetAlarm(false)
            }
        }
    }
}

