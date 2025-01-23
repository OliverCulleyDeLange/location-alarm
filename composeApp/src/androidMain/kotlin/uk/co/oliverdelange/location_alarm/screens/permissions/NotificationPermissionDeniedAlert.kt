package uk.co.oliverdelange.location_alarm.screens.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.oliverdelange.locationalarm.strings.MapScreenStrings

@Composable
fun NotificationPermissionDeniedAlert(
    shouldShowRationale: Boolean,
    requestPermissions: () -> Unit
) {
    Column(
        Modifier
            .padding(horizontal = 40.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(16.dp)
    ) {
        Text(
            text = "Notification Permission Denied",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = MapScreenStrings.notificationPermissionDeniedText,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (shouldShowRationale) {
            TextButton(onClick = { requestPermissions() }) {
                Text(
                    text = "Allow Notification Permissions",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Text(
                "Please go to settings to enable notification permissions",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
private fun NotificationPermissionDeniedAlert_Preview() {
    Column {
        NotificationPermissionDeniedAlert(true) { }
        NotificationPermissionDeniedAlert(false) { }
    }
}