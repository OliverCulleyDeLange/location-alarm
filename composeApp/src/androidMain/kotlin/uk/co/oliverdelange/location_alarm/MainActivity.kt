package uk.co.oliverdelange.location_alarm

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import uk.co.oliverdelange.location_alarm.screens.AppUi
import uk.co.oliverdelange.location_alarm.service.LocationAlarmService
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.model.domain.DebugMode
import uk.co.oliverdelange.locationalarm.model.domain.DebugMode.VolumeButton.Down
import uk.co.oliverdelange.locationalarm.model.domain.DebugMode.VolumeButton.Up
import uk.co.oliverdelange.locationalarm.store.AppStateStore

class MainActivity : ComponentActivity() {

    private val appStateStore: AppStateStore by inject()
    private val debugMode: DebugMode by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        setContent {
            AppUi(appStateStore)
        }

        lifecycleScope.launch {
            // Start and stop the foreground service based on alarm enabled state
            appStateStore.state.map { it.alarmEnabled }.distinctUntilChanged().collect { alarmEnabled ->
                val intent = Intent(applicationContext, LocationAlarmService::class.java)
                if (alarmEnabled) {
                    SLog.i("Starting LocationAlarmService")
                    startForegroundService(intent)
                } else {
                    SLog.i("Stopping LocationAlarmService")
                    stopService(intent)
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        appStateStore.onAppForegrounded()
        super.onStart()
    }

    override fun onStop() {
        appStateStore.onAppBackgrounded()
        super.onStop()
    }

    override fun onDestroy() {
        SLog.w("onDestroy MainActivity")
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> debugMode.onVolumeButton(Down)
            KeyEvent.KEYCODE_VOLUME_UP -> debugMode.onVolumeButton(Up)
        }
        return super.onKeyDown(keyCode, event)
    }
}
