package uk.co.oliverdelange.location_alarm.screens.debug

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.ui.AutoScrollToBottom
import uk.co.oliverdelange.locationalarm.model.ui.debug.DebugViewModel
import uk.co.oliverdelange.locationalarm.model.ui.debug.GpsUiState
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogColor
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogEntryUiModel
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogUiState

@Composable
fun DebugScreen() {
    val viewModel: DebugViewModel = koinViewModel()
    val logState by viewModel.logUiState.collectAsStateWithLifecycle()
    val gpsState by viewModel.gpsUiState.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Logs", "Gps")

    Scaffold(
        modifier = Modifier.safeContentPadding(),
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> Logs(logState)
                1 -> Gps(gpsState)
            }
        }
    }

}

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

@Composable
private fun Logs(state: LogUiState) {
    AutoScrollToBottom(state.logs.size) { scrollState ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .padding(8.dp)
        ) {
            items(state.logs) {
                val string = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(it.date + "\t")
                    }
                    withStyle(SpanStyle(color = getLogColor(it))) {
                        append(it.message)
                    }
                }

                Text(string, fontSize = 10.sp)
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