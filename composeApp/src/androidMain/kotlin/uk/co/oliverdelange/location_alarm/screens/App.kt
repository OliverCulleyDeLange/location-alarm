package uk.co.oliverdelange.location_alarm.screens

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
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
import model.domain.PermissionState
import model.domain.RequestablePermission
import model.domain.granted
import model.ui.MapUiState
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.screens.map.MapScreen
import uk.co.oliverdelange.location_alarm.screens.permissions.LocationPermissionsDeniedScreen
import uk.co.oliverdelange.location_alarm.screens.permissions.LocationPermissionsRequiredScreen
import uk.co.oliverdelange.location_alarm.ui.PermissionHandler
import uk.co.oliverdelange.location_alarm.ui.PermissionsHandler
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme

@Composable
@Preview
fun App(viewModel: MapUiViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(MapUiState())

    AppTheme {
        // Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionHandler(POST_NOTIFICATIONS, state.userRequestedAlarmEnable) {
                viewModel.onNotificationPermissionResult(it)
            }
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
            if (state.locationPermissionState.granted()) {
                MapScreen(
                    state,
                    uiState.toggleAlarmButtonText,
                    onLocationUpdate = { locations -> viewModel.onLocationChange(locations) },
                    onMapTap = { location -> viewModel.onMapTap(location) },
                    onToggleAlarm = { viewModel.onToggleAlarm() },
                    onToggleAlarmWithDelay = { viewModel.onToggleAlarmWithDelay(2) },
                    onRadiusChange = { radius -> viewModel.onRadiusChanged(radius) },
                    onTapLocationIcon = { viewModel.onTapLocationIcon() },
                    onFinishFlyingToUsersLocation = { viewModel.onFinishFlyingToUsersLocation() }
                )
            } else if (state.locationPermissionState == PermissionState.Unknown) {
                LocationPermissionsRequiredScreen {
                    viewModel.onTapAllowLocationPermissions()
                }
            } else {
                LocationPermissionsDeniedScreen()
            }
            AlarmAlert(state.alarmTriggered) {
                viewModel.onTapStopAlarm()
            }
        }
    }
}

