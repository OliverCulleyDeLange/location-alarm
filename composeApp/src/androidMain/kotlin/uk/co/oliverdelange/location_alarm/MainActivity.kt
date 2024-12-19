package uk.co.oliverdelange.location_alarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.screens.App
import uk.co.oliverdelange.location_alarm.screens.MapUiViewModel
import uk.co.oliverdelange.location_alarm.service.LocationAlarmService

class MainActivity : ComponentActivity() {

    private val appViewModel: MapUiViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        setContent {
            App(appViewModel)
        }

        lifecycleScope.launch {
            // Start and stop the foreground service based on alarm enabled state
            appViewModel.state.map { it.alarmEnabled }.distinctUntilChanged().collect { alarmEnabled ->
                val intent = Intent(applicationContext, LocationAlarmService::class.java)
                if (alarmEnabled && !LocationAlarmService.isRunning) {
                    Timber.i("Starting LocationAlarmService")
                    startForegroundService(intent)
                } else if (!alarmEnabled && LocationAlarmService.isRunning) {
                    Timber.i("Stopping LocationAlarmService")
                    stopService(intent)
                }
            }
        }
        super.onCreate(savedInstanceState)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}