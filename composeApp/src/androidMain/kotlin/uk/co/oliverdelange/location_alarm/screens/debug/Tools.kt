package uk.co.oliverdelange.location_alarm.screens.debug

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import uk.co.oliverdelange.locationalarm.logging.SLog

@Composable
fun Tools() {
    val ctx = LocalContext.current
    Column {
        Button(onClick = { throw RuntimeException("Test crash") }) {
            Text("Crash")
        }
        Button(onClick = { openFsiSettings(ctx) }) {
            Text("FSI Settings")
        }
    }
}

fun openFsiSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        val canUseFsi = notificationManager.canUseFullScreenIntent()
        SLog.d("Opening FullScreenIntent settings. canUseFsi: $canUseFsi")
        context.startActivity(intent)
    } else {
        SLog.w("Device does not support FSI")
    }
}
