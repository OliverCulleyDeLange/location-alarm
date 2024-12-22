package uk.co.oliverdelange.location_alarm.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.screens.map.MapScreen
import uk.co.oliverdelange.location_alarm.screens.permissions.LocationPermissionsDeniedScreen
import uk.co.oliverdelange.location_alarm.screens.permissions.LocationPermissionsRequiredScreen
import uk.co.oliverdelange.location_alarm.ui.PermissionsHandler
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme
import uk.co.oliverdelange.locationalarm.model.domain.RequestablePermission
import uk.co.oliverdelange.locationalarm.model.ui.MapUiScreenState
import uk.co.oliverdelange.locationalarm.model.ui.MapUiState
import uk.co.oliverdelange.locationalarm.model.ui.UiResult
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent

@SuppressLint("InlinedApi")
@Composable
@Preview
fun AppUi(viewModel: MapUiViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle(MapUiState())

    AppTheme {
        // Permissions
        PermissionsHandler(
            permission = RequestablePermission.Notifications,
            shouldRequestPermission = state.shouldRequestNotificationPermissions,
            onRequestedPermissions = { viewModel.onEvent(UiResult.RequestedNotificationPermission) },
            onPermissionChanged = { viewModel.onEvent(UiResult.NotificationPermissionResult(it)) }
        )
        PermissionsHandler(
            permission = RequestablePermission.Location,
            shouldRequestPermission = state.shouldRequestLocationPermissions,
            onRequestedPermissions = { viewModel.onEvent(UiResult.RequestedLocationPermission) },
            onPermissionChanged = { viewModel.onEvent(UiResult.LocationPermissionResult(it)) }
        )

        // UI
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (state.screenState) {
                MapUiScreenState.ShowMap -> MapScreen(state) {
                    viewModel.onEvent(it)
                }

                MapUiScreenState.LocationPermissionRequired -> LocationPermissionsRequiredScreen {
                    viewModel.onEvent(UserEvent.TappedAllowLocationPermissions)
                }

                MapUiScreenState.LocationPermissionDenied -> LocationPermissionsDeniedScreen()
            }
            AlarmAlert(state.shouldShowAlarmAlert) {
                viewModel.onEvent(UserEvent.TappedStopAlarm)
            }
        }
    }
}

