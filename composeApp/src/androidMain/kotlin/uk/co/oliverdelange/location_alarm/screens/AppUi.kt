package uk.co.oliverdelange.location_alarm.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.ui.PermissionsHandler
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme
import uk.co.oliverdelange.locationalarm.model.domain.RequestablePermission
import uk.co.oliverdelange.locationalarm.store.AppStateStore

@Composable
@Preview
fun AppUi(appStateStore: AppStateStore) {
    val state by appStateStore.state.collectAsStateWithLifecycle()

    AppTheme {
        // Permissions
        PermissionsHandler(
            permission = RequestablePermission.Notifications,
            shouldCheckPermission = state.shouldCheckNotificationPermissions,
            shouldRequestPermission = state.shouldRequestNotificationPermissions,
            onRequestedPermissions = { appStateStore.onRequestedNotificationPermissions() },
            onPermissionChanged = { appStateStore.onNotificationPermissionResult(it) },
            onPermissionChecked = { appStateStore.onNotificationPermissionChecked() }
        )
        PermissionsHandler(
            permission = RequestablePermission.Location,
            shouldCheckPermission = state.shouldCheckLocationPermissions,
            shouldRequestPermission = state.shouldRequestLocationPermissions,
            onRequestedPermissions = { appStateStore.onRequestedLocationPermissions() },
            onPermissionChanged = { appStateStore.onLocationPermissionResult(it) },
            onPermissionChecked = { appStateStore.onLocationPermissionChecked() }
        )

        Navigation(state) {
            appStateStore.didNavigate(it)
        }
    }
}

