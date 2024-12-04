package uk.co.oliverdelange.location_alarm.permissions

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission

fun ComponentActivity.checkPermissionAndDo(
    permission: String,
    doOnPermissionGranted: () -> Unit,
    doOnPermissionDenied: (String) -> Unit,
    doOnRationaleRequired: (String) -> Unit,
) {
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) doOnPermissionGranted() else doOnPermissionDenied(permission)
    }

    when {
        checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
            doOnPermissionGranted()
        }

        shouldShowRequestPermissionRationale(this, permission) -> {
            doOnRationaleRequired(permission)
        }

        else -> {
            requestPermissionLauncher.launch(permission)
        }
    }
}