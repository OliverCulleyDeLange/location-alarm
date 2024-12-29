package uk.co.oliverdelange.location_alarm.screens.debug

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.model.ui.debug.DebugViewModel
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogColor
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogEntryUiModel

@Composable
fun DebugScreen() {
    val viewModel: DebugViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .safeContentPadding()
            .padding(8.dp)
    ) {
        items(state.logs) {
            Text("${it.date} ${it.message}", color = getLogColor(it), fontSize = 10.sp)
        }
        item {
            Button(onClick = { SLog.e("TEST") }) {
                Text("Log")
            }
        }
    }
}

@Composable
private fun getLogColor(it: LogEntryUiModel) = when (it.color) {
    LogColor.Red -> colorResource(R.color.red)
    LogColor.Orange -> colorResource(R.color.orange)
    LogColor.Green -> colorResource(R.color.green)
    LogColor.Blue -> colorResource(R.color.blue)
    LogColor.Grey -> colorResource(R.color.grey)
}