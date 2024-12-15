package uk.co.oliverdelange.location_alarm.screens

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import model.ui.MapUiState
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.screens.map.MapScreen
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview
fun App(viewmodel: MapUiViewModel = viewModel()) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle(MapUiState())

    val notificationPermissionState = rememberPermissionState(POST_NOTIFICATIONS)
    val locationPermissionState = rememberMultiplePermissionsState(listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))

    AppTheme {
        // Sync ui permissions with state
        LaunchedEffect(notificationPermissionState.status.isGranted) {
            viewmodel.onNotificationPermissionResult(notificationPermissionState.status.isGranted)
        }
        LaunchedEffect(locationPermissionState.allPermissionsGranted) {
            viewmodel.onLocationPermissionResult(locationPermissionState.allPermissionsGranted)
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
                    onRadiusChange = { radius -> viewmodel.onRadiusChanged(radius) },
                    onTapLocationIcon = { viewmodel.onTapLocationIcon() },
                    onFinishFlyingToUsersLocation = { viewmodel.onFinishFlyingToUsersLocation() }
                )
            } else {
                Column(
                    Modifier
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "This app needs your location to enable location based alarms. Please allow precise location access for the app to work.",
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { locationPermissionState.launchMultiplePermissionRequest() },
                    ) {
                        Text("Allow Location Access")
                    }
                }
            }
            AlarmAlert(state.alarmTriggered) {
                viewmodel.onTapStopAlarm()
            }
        }
    }
}

