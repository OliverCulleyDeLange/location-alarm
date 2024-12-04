package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        MapboxMap(
            state.perimeterRadiusMeters,
            state.geoFenceLocation,
            state.locationPermissionState.granted(),
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
        Button(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = onToggleAlarm,
        ) {
            Text(alarmButtonText)
        }
    }
}

@Preview
@Composable
fun Preview_MapScreen() = MapScreen(AppState(), "Enable Alarm", {}, {}, {}, {})