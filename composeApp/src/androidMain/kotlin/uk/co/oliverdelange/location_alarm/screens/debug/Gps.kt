package uk.co.oliverdelange.location_alarm.screens.debug

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.co.oliverdelange.location_alarm.ui.AutoScrollToBottom
import uk.co.oliverdelange.locationalarm.model.ui.debug.GpsUiState

@Composable
fun Gps(state: GpsUiState) {
    AutoScrollToBottom(state.gps.size) { scrollState ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .padding(8.dp)
        ) {
            items(state.gps) {
                Text(it.date)
            }
        }
    }
}