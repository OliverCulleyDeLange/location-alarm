package uk.co.oliverdelange.location_alarm.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme

@Composable
fun AlarmAlert(
    alarmTriggered: Boolean,
    onStopAlarm: () -> Unit,
) {
    if (alarmTriggered) {
        AlertDialog(
            title = {
                Text(text = "Wakey wakey!")
            },
            text = {
                Text(text = "You have reached your destination")
            },
            onDismissRequest = {
                onStopAlarm()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onStopAlarm()
                    }
                ) {
                    Text("Stop Alarm")
                }
            },
        )
    }
}

@PreviewLightDark
@Composable
fun AlarmAlert_Preview() = AppTheme { AlarmAlert(true, {}) }