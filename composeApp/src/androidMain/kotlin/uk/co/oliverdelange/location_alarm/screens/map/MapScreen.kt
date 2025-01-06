package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.co.oliverdelange.location_alarm.screens.AlarmAlert
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent
import uk.co.oliverdelange.locationalarm.model.ui.map.MapViewModel

@Composable
fun MapScreen() {
    val viewModel: MapViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle(viewModel.state.value)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MapScreenContent(state) {
            viewModel.onEvent(it)
        }
    }
    AlarmAlert(state.shouldShowAlarmAlert) {
        viewModel.onEvent(UserEvent.TappedStopAlarm)
    }
}

