package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.domain.AppState
import model.domain.Location
import model.domain.granted

@Composable
fun MapScreen(
    state: AppState,
    alarmButtonText: String,
    onMapTap: (Location) -> Unit,
    onLocationUpdate: (List<Location>) -> Unit,
    onToggleAlarm: () -> Unit,
    onRadiusChange: (Int) -> Unit,
) {
    Box {
        val darkMode = isSystemInDarkTheme()
        MapboxMap(
            state.perimeterRadiusMeters,
            state.geoFenceLocation,
            state.locationPermissionState.granted(),
            darkMode,
            onLocationUpdate = onLocationUpdate,
            onMapTap = onMapTap,
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
            Column(
                Modifier
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(8.dp)
            ) {
                Text("${state.distanceToGeofence}m -> Destination", color = MaterialTheme.colorScheme.secondary)
                Text("${state.distanceToGeofencePerimeter}m -> Alarm", color = MaterialTheme.colorScheme.secondary)
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp, top = 8.dp),
                onClick = onToggleAlarm,
                elevation = ButtonDefaults.elevatedButtonElevation()
            ) {
                Text(
                    text = alarmButtonText.uppercase(),
                    fontSize = 24.sp,
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview_MapScreen() = MapScreen(AppState(), "Enable Alarm", {}, {}, {}, {})