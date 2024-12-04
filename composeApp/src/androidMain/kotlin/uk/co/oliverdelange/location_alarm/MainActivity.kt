package uk.co.oliverdelange.location_alarm

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.permissions.checkPermissionAndDo
import uk.co.oliverdelange.location_alarm.screens.App
import uk.co.oliverdelange.location_alarm.screens.AppViewModel

class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        checkPermissionAndDo(POST_NOTIFICATIONS,
            doOnPermissionGranted = {
                appViewModel.onNotificationPermissionResult(true)
            },
            doOnPermissionDenied = {
                appViewModel.onNotificationPermissionResult(false)
            },
            doOnRationaleRequired = {
                Timber.w("Rational required for $it")
                TODO()
            }
        )

        checkPermissionAndDo(ACCESS_FINE_LOCATION,
            doOnPermissionGranted = {
                appViewModel.onLocationPermissionResult(true)
            },
            doOnPermissionDenied = {
                appViewModel.onLocationPermissionResult(false)
            },
            doOnRationaleRequired = {
                Timber.w("Rational required for $it")
                TODO()
            })

        setContent {
            App(appViewModel)
        }
        super.onCreate(savedInstanceState)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}