package uk.co.oliverdelange.location_alarm.screens

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import model.domain.PermissionState
import model.ui.MapUiState
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.screens.map.MapScreen
import uk.co.oliverdelange.location_alarm.screens.permissions.LocationPermissionsDeniedScreen
import uk.co.oliverdelange.location_alarm.screens.permissions.LocationPermissionsRequiredScreen
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview
fun App(viewmodel: MapUiViewModel = viewModel()) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle(MapUiState())

    val notificationPermissionState = rememberPermissionState(POST_NOTIFICATIONS)
    val locationPermissionState = rememberMultiplePermissionsState(listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    var hasRequestedLocationPermissions by remember { mutableStateOf(false) }

    AppTheme {
        // Sync ui permissions with state
        LaunchedEffect(notificationPermissionState.status.isGranted) {
            viewmodel.onNotificationPermissionResult(notificationPermissionState.status.isGranted)
        }
        LaunchedEffect(locationPermissionState.permissions, hasRequestedLocationPermissions) {
//            Timber.e("OCD OCD $hasRequestedLocationPermissions ${locationPermissionState.permissions.joinToString { it.permission + "" + it.status }}")
            viewmodel.onLocationPermissionResult(when {
                locationPermissionState.allPermissionsGranted -> PermissionState.Granted
                hasRequestedLocationPermissions && locationPermissionState.permissions
                    .any { it.status is PermissionStatus.Denied } -> PermissionState.Denied

                else -> PermissionState.Unknown
            })
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (locationPermissionState.allPermissionsGranted) {
                MapScreen(
                    state,
                    uiState.toggleAlarmButtonText,
                    onLocationUpdate = { locations -> viewmodel.onLocationChange(locations) },
                    onMapTap = { location -> viewmodel.onMapTap(location) },
                    onToggleAlarm = {
                        // FIXME Move out of click handler. Request permissions based on bool from state
                        if (!notificationPermissionState.status.isGranted) {
                            notificationPermissionState.launchPermissionRequest()
                        }
                        viewmodel.onToggleAlarm()
                    },
                    onToggleAlarmWithDelay = {
                        // FIXME Move out of click handler. Request permissions based on bool from state
                        //FIXME DRY
                        if (!notificationPermissionState.status.isGranted) {
                            notificationPermissionState.launchPermissionRequest()
                        }
                        viewmodel.onToggleAlarmWithDelay(2)
                    },
                    onRadiusChange = { radius -> viewmodel.onRadiusChanged(radius) },
                    onTapLocationIcon = { viewmodel.onTapLocationIcon() },
                    onFinishFlyingToUsersLocation = { viewmodel.onFinishFlyingToUsersLocation() },
                    onRequestNotificationPermissions = { notificationPermissionState.launchPermissionRequest() }
                )
            } else if (state.locationPermissionState == PermissionState.Unknown) {
                LocationPermissionsRequiredScreen {
                    locationPermissionState.launchMultiplePermissionRequest()
                    hasRequestedLocationPermissions = true
                }
            } else {
                LocationPermissionsDeniedScreen()
            }
            AlarmAlert(state.alarmTriggered) {
                viewmodel.onTapStopAlarm()
            }
        }
    }
}

