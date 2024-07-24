package uk.co.oliverdelange.location_alarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import uk.co.oliverdelange.location_alarm.screens.App

class MainActivity : ComponentActivity(), PermissionsListener {

    private lateinit var permissionsManager: PermissionsManager
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            appViewModel.onPermissionResult(true)
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }

        setContent {
            App()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(granted: Boolean) {
        appViewModel.onPermissionResult(granted)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}