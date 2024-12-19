package uk.co.oliverdelange.location_alarm.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState

/**
 * A horribly complicated wrapper to handle the fact that the android permissions
 * model doesn't tell you when the user hasn't granted any permissions yet
 * TODO Use [PermissionsHandler] instead as its simplified into domain logic now
 * */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permission: String,
    shouldRequestPermission: Boolean,
    onPermissionChanged: (PermissionState) -> Unit
) {
    var hasRequestedPermissions by remember { mutableStateOf(false) }

    val permissionState = rememberPermissionState(permission) {
        hasRequestedPermissions = true
    }

    LaunchedEffect(shouldRequestPermission) {
        if (shouldRequestPermission) permissionState.launchPermissionRequest()
    }

    // Boolean state variable to track if shouldShowRationale changed from true to false
    var shouldShowRationaleBecameFalseFromTrue by remember { mutableStateOf(false) }

    // Remember the previous value of shouldShowRationale
    var prevShouldShowRationale by remember { mutableStateOf(permissionState.status.shouldShowRationale) }

    // Track changes in shouldShowRationale
    LaunchedEffect(permissionState.status.shouldShowRationale) {
        if (prevShouldShowRationale && !permissionState.status.shouldShowRationale) {
            shouldShowRationaleBecameFalseFromTrue = true
        }
        prevShouldShowRationale = permissionState.status.shouldShowRationale
    }

    // if shouldShowRationale changed from true to false and the permission is not granted,
    // then the user denied the permission and checked the "Never ask again" checkbox
    val userDeniedPermission = shouldShowRationaleBecameFalseFromTrue && !permissionState.status.isGranted

    // If we have requested permissions, and after doing so the permission is denied and shouldShowRationale is unchanged
    // This means that the user has fully denied the permission
    LaunchedEffect(hasRequestedPermissions) {
        if (hasRequestedPermissions && !permissionState.status.isGranted && prevShouldShowRationale == permissionState.status.shouldShowRationale) {
            onPermissionChanged(PermissionState.Denied(permissionState.status.shouldShowRationale))
        }
    }

    if (userDeniedPermission) {
        onPermissionChanged(PermissionState.Denied(permissionState.status.shouldShowRationale))
    } else if (permissionState.status.isGranted) {
        onPermissionChanged(PermissionState.Granted)
    } else if (permissionState.status.shouldShowRationale) {
        onPermissionChanged(PermissionState.Denied(permissionState.status.shouldShowRationale))
    } else {
        onPermissionChanged(PermissionState.Unknown)
    }
}