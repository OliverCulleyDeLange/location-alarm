package uk.co.oliverdelange.location_alarm.screens.map

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import uk.co.oliverdelange.location_alarm.helpers.isDebug
import uk.co.oliverdelange.location_alarm.mapbox.buildGeofenceFeature
import uk.co.oliverdelange.location_alarm.mapper.domain_to_ui.toPoint
import uk.co.oliverdelange.location_alarm.screens.permissions.NotificationPermissionDeniedAlert
import uk.co.oliverdelange.locationalarm.mapbox.MapboxIDs
import uk.co.oliverdelange.locationalarm.model.ui.MapUiState

@OptIn(MapboxExperimental::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    state: MapUiState,
    alarmButtonText: String,
    onMapTap: (uk.co.oliverdelange.locationalarm.model.domain.Location) -> Unit,
    onLocationUpdate: (List<uk.co.oliverdelange.locationalarm.model.domain.Location>) -> Unit,
    onToggleAlarm: () -> Unit,
    onToggleAlarmWithDelay: () -> Unit,
    onRadiusChange: (Int) -> Unit,
    onTapLocationIcon: () -> Unit,
    onFinishFlyingToUsersLocation: () -> Unit,
) {
    Box {
        val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(POST_NOTIFICATIONS)
        } else null

        val geofenceSourceState = rememberGeoJsonSourceState(sourceId = MapboxIDs.SOURCE_GEOFENCE)
        // Update geofence geojson source only when geofence location or radius changes
        LaunchedEffect(state.geoFenceLocation, state.perimeterRadiusMeters) {
            state.geoFenceLocation?.toPoint()?.let {
                geofenceSourceState.data = GeoJSONData(buildGeofenceFeature(it, state.perimeterRadiusMeters))
            }
        }
        MapboxMap(
            isSystemInDarkTheme(),
            state.usersLocationToFlyTo,
            state.shouldEnableMapboxLocationComponent,
            onLocationUpdate = onLocationUpdate,
            onMapTap = onMapTap,
            onFinishFlyingToUsersLocation = onFinishFlyingToUsersLocation,
            geofenceSourceState = geofenceSourceState,
        )
        FlyToCurrentLocationButton(
            onTapLocationIcon,
            Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
        )

        RadiusScrubber(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            radius = state.perimeterRadiusMeters,
            onRadiusChange = onRadiusChange
        )
        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.End
        ) {
            if (state.shouldShowNotificationPermissionDeniedMessage) {
                NotificationPermissionDeniedAlert(
                    state.shouldShowNotificationPermissionRationale,
                    requestPermissions = { notificationPermissionState?.launchPermissionRequest() }
                )
            }
            if (state.shouldShowDistanceToAlarmText) {
                Column(
                    Modifier
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp)
                ) {
                    Text(state.distanceToAlarmText, color = MaterialTheme.colorScheme.secondary)
                }
            }
            if (isDebug()) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 8.dp, top = 8.dp),
                    onClick = onToggleAlarmWithDelay,
                ) {
                    Text("Delayed start")
                }
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp, top = 8.dp),
                onClick = onToggleAlarm,
                elevation = ButtonDefaults.elevatedButtonElevation(),
                enabled = state.enableAlarmButtonEnabled
            ) {
                Text(
                    text = alarmButtonText.uppercase(),
                    fontSize = 24.sp,
                )
            }
        }
    }
}

@Composable
private fun FlyToCurrentLocationButton(onTapLocationIcon: () -> Unit, modifier: Modifier) {
    IconButton(
        onClick = { onTapLocationIcon() },
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 24.dp) // Make same padding as main button
            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .size(40.dp)
    ) {
        Icon(
            Icons.Default.LocationOn, null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview
@Composable
fun Preview_MapScreen() = MapScreen(MapUiState(), "Enable Alarm", {}, {}, {}, {}, {}, {}, {})