package uk.co.oliverdelange.location_alarm

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.koin.android.ext.android.inject
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.store.AppStateStore

class AlarmCancelActivity : ComponentActivity() {

    private val appStateStore: AppStateStore by inject()

    init {
        SLog.v("AlarmCancelActivity init")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContent {
            AppTheme {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(painterResource(R.drawable.ic_location_alarm), null)
                        Spacer(Modifier.height(16.dp))
                        Text("You have reached your destination")
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = {
                            appStateStore.onSetAlarm(false)
                            this@AlarmCancelActivity.finish()
                        }) {
                            Text("Cancel Alarm")
                        }
                    }
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        SLog.v("onDestroy AlarmCancelActivity")
        super.onDestroy()
    }
}
