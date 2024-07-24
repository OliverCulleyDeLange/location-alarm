package uk.co.oliverdelange.location_alarm.screens

import Greeting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.AppViewModel
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.granted

@OptIn(MapboxExperimental::class)
@Composable
@Preview
fun App(viewmodel: AppViewModel = viewModel()) {
    val state by viewmodel.state.collectAsStateWithLifecycle()

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(R.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = MapViewportState().apply {
                    setCameraOptions {
                        zoom(2.0)
                        center(Point.fromLngLat(-98.0, 39.5))
                        pitch(0.0)
                        bearing(0.0)
                    }
                },

                ) {
                val locationPermissionGranted = state.locationPermissionState.granted()
                MapEffect(locationPermissionGranted) { mapView ->
                    with(mapView) {
                        if (locationPermissionGranted) {
                            location.locationPuck = createDefault2DPuck(withBearing = false)
                            location.enabled = true
                            viewport.transitionTo(
                                targetState = viewport.makeFollowPuckViewportState(),
                                transition = viewport.makeImmediateViewportTransition()
                            )
                        } else {
                            location.enabled = false
                        }
                    }
                }
            }
        }
    }
}