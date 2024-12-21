package uk.co.oliverdelange.location_alarm.screens

import android.Manifest.permission.POST_NOTIFICATIONS
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
import uk.co.oliverdelange.location_alarm.ui.PermissionHandler
import uk.co.oliverdelange.location_alarm.ui.PermissionsHandler
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme
import uk.co.oliverdelange.locationalarm.model.domain.RequestablePermission
import uk.co.oliverdelange.locationalarm.model.ui.MapUiScreenState
import uk.co.oliverdelange.locationalarm.model.ui.MapUiState

@SuppressLint("InlinedApi")
@Composable
@Preview
fun App(viewModel: MapUiViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle(MapUiState())

    AppTheme {
        // Permissions
        PermissionHandler(POST_NOTIFICATIONS, state.shouldRequestNotificationPermissions) {
            viewModel.onNotificationPermissionResult(it)
        }
        PermissionsHandler(
            permission = RequestablePermission.Location,
            shouldRequestPermission = state.shouldRequestLocationPermissions,
            onRequestedPermissions = { viewModel.onRequestedLocationPermissions() }
        ) {
            viewModel.onLocationPermissionResult(it)
        }

        // UI
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (state.screenState) {
                MapUiScreenState.ShowMap -> MapScreen(state, viewModel)

                MapUiScreenState.LocationPermissionRequired -> LocationPermissionsRequiredScreen {
                    viewModel.onTapAllowLocationPermissions()
                }

                MapUiScreenState.LocationPermissionDenied -> LocationPermissionsDeniedScreen()
            }
            AlarmAlert(state.shouldShowAlarmAlert) {
                viewModel.onTapStopAlarm()
            }
        }
    }
}

