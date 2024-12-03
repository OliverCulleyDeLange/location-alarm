package uk.co.oliverdelange.location_alarm.screens

import Greeting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.screens.map.MapScreen

@Composable
@Preview
fun App(viewmodel: AppViewModel = viewModel()) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val alarmButtonText by viewmodel.toggleAlarmButtonText.collectAsStateWithLifecycle("")

    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            val greeting = remember { Greeting().greet() }
            Text("Compose: $greeting")
            Text("Alarm: ${state.alarmTriggered}")
            MapScreen(
                state,
                alarmButtonText,
                onLocationUpdate = { locations -> viewmodel.onLocationChange(locations) },
                onMapTap = { location -> viewmodel.onMapTap(location) },
                onToggleAlarm = { viewmodel.onToggleAlarm() },
                onRadiusChange = { radius -> viewmodel.onRadiusChanged(radius) }
            )
        }
    }
}