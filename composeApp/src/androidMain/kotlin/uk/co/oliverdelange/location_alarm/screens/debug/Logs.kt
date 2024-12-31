package uk.co.oliverdelange.location_alarm.screens.debug

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.ui.AutoScrollToBottom
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogColor
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogEntryUiModel
import uk.co.oliverdelange.locationalarm.model.ui.debug.LogUiState

@Composable
fun Logs(state: LogUiState) {
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