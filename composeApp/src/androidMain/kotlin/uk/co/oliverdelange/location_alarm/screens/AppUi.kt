package uk.co.oliverdelange.location_alarm.screens

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.ui.PermissionsHandler
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme
import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.model.domain.AppStateStore
import uk.co.oliverdelange.locationalarm.model.domain.RequestablePermission

@SuppressLint("InlinedApi")
@Composable
@Preview
fun AppUi(appStateStore: AppStateStore) {
    val state by appStateStore.state.collectAsStateWithLifecycle(AppState())

    AppTheme {
        // Permissions
        PermissionsHandler(
            permission = RequestablePermission.Notifications,
            shouldRequestPermission = state.shouldRequestNotificationPermissions,
            onRequestedPermissions = { appStateStore.onRequestedNotificationPermissions() },
            onPermissionChanged = { appStateStore.onNotificationPermissionResult(it) }
        )
        PermissionsHandler(
            permission = RequestablePermission.Location,
            shouldRequestPermission = state.shouldRequestLocationPermissions,
            onRequestedPermissions = { appStateStore.onRequestedLocationPermissions() },
            onPermissionChanged = { appStateStore.onLocationPermissionResult(it) }
        )

        Navigation(appStateStore, state)
    }
}

