package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import mapbox.MapboxIDs
import model.domain.Location
import model.domain.MapFeatureState
import model.domain.granted
import uk.co.oliverdelange.location_alarm.helpers.isDebug
import uk.co.oliverdelange.location_alarm.mapbox.buildGeofenceFeature
import uk.co.oliverdelange.location_alarm.mapper.domain_to_ui.toPoint
import uk.co.oliverdelange.location_alarm.screens.permissions.NotificationPermissionDeniedAlert

@OptIn(MapboxExperimental::class)
@Composable
fun MapScreen(
    state: MapFeatureState,
    alarmButtonText: String,
    onMapTap: (Location) -> Unit,
    onLocationUpdate: (List<Location>) -> Unit,
    onToggleAlarm: () -> Unit,
    onToggleAlarmWithDelay: () -> Unit,
    onRadiusChange: (Int) -> Unit,
    onTapLocationIcon: () -> Unit,
    onFinishFlyingToUsersLocation: () -> Unit,
    onRequestNotificationPermissions: () -> Unit,
) {
    Box {
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
            state.locationPermissionState.granted(),
            onLocationUpdate = onLocationUpdate,
            onMapTap = onMapTap,
            onFinishFlyingToUsersLocation = onFinishFlyingToUsersLocation,
            geofenceSourceState = geofenceSourceState,
        )
        FlyToCurrentLocationButton(
            onTapLocationIcon,
            Modifier.align(Alignment.BottomStart)
        )

        RadiusScrubber(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            radius = state.perimeterRadiusMeters,
            onRadiusChange = onRadiusChange
        )
        Column(
            Modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.End
        ) {
            if (state.shouldShowNotificationPermissionDeniedMessage) {
                NotificationPermissionDeniedAlert(
                    requestPermissions = { onRequestNotificationPermissions() }
                )
            }
            if (state.alarmEnabled) {
                Column(
                    Modifier
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp)
                ) {
                    Text("${state.distanceToGeofencePerimeter}m -> Alarm", color = MaterialTheme.colorScheme.secondary)
                }
            }
            if (isDebug()) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp, top = 8.dp),
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
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .size(40.dp)
            .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
    ) {
        Icon(
            Icons.Default.LocationOn, null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview
@Composable
fun Preview_MapScreen() = MapScreen(MapFeatureState(), "Enable Alarm", {}, {}, {}, {}, {}, {}, {}, {})