package uk.co.oliverdelange.location_alarm.screens.permissions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.co.oliverdelange.locationalarm.model.ui.UserEvent
import uk.co.oliverdelange.locationalarm.model.ui.location_permission_required.LocationPermissionRequiredViewModel

@Composable
fun LocationPermissionsRequiredScreen() {
    val viewModel: LocationPermissionRequiredViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = state.shouldShowContent,
        enter = fadeIn(tween(1000)),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "This app needs your location to enable location based alarms. Please allow precise location access for the app to work.",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.onEvent(UserEvent.TappedAllowLocationPermissions) },
            ) {
                Text("Allow Location Access")
            }
        }
    }
}