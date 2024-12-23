package uk.co.oliverdelange.location_alarm.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import uk.co.oliverdelange.location_alarm.permissions.AndroidSystemPermissionState
import uk.co.oliverdelange.location_alarm.permissions.Permissions
import uk.co.oliverdelange.location_alarm.permissions.androidPermissionStrings
import uk.co.oliverdelange.locationalarm.logging.Log
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState
import uk.co.oliverdelange.locationalarm.model.domain.RequestablePermission
import java.time.LocalDateTime

/** Handles permission state changes in compose */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsHandler(
    permission: RequestablePermission,
    shouldRequestPermission: Boolean,
    onRequestedPermissions: () -> Unit,
    onPermissionChanged: (PermissionState) -> Unit
) {
    var permissionsUpdatedAt: LocalDateTime? by remember { mutableStateOf(null) }

    val permissionsLogic = remember {
        Permissions(permission) {
            onPermissionChanged(it)
        }
    }

    val permissionState = rememberMultiplePermissionsState(permission.androidPermissionStrings()) {
        permissionsUpdatedAt = LocalDateTime.now()
    }

    LaunchedEffect(permissionsUpdatedAt) {
        val state = permissionState.permissions
            .map { AndroidSystemPermissionState(it.permission, it.status.isGranted, it.status.shouldShowRationale) }
        permissionsLogic.onPermissionsChanged(state)
        // The first time this launched effect is called isn't due to a user action,
        // but because of the initial value of permissionsUpdatedAt
        // This is required to set the initial permission state, but we don't want to
        // treat this as a user responding to a permission dialog.
        permissionsUpdatedAt?.let {
            permissionsLogic.onRequestedPermissions()
        }
    }

    LaunchedEffect(shouldRequestPermission) {
        if (shouldRequestPermission) {
            Log.d("Launching $permission permission request")
            permissionState.launchMultiplePermissionRequest()
            onRequestedPermissions()
        }
    }
}