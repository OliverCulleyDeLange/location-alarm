package uk.co.oliverdelange.location_alarm.screens

import Greeting
import android.util.Log
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.toColor
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.style.expressions.dsl.generated.color
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.extension.style.sources.updateGeoJSONSourceFeatures
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport
import org.jetbrains.compose.ui.tooling.preview.Preview
import uk.co.oliverdelange.location_alarm.AppState
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
                val color = colorResource(R.color.geofenceBorder)
                MapEffect(Unit) { mapView ->
                    mapView.mapboxMap.loadStyle(
                        style(style = Style.LIGHT) {
                            +geoJsonSource(id = "source-geofence") {
                                feature(state.geofenceFeature)
                            }
                            +fillLayer(layerId = "layer-geofence-fill", sourceId = "source-geofence") {
                                fillColor(color.toArgb())
                                fillOpacity(0.3)
                                fillOutlineColor(color(R.color.geofenceBorder.toColor().toArgb()))
                            }
                            +lineLayer(layerId = "layer-geofence-line", sourceId = "source-geofence") {
                                lineWidth(5.0)
                                lineColor(color.toArgb())
                            }
                        }
                    ) { style ->
                        Log.d("OCD", "Updating style after initialisation")
                        updateGeofence(style, state)
                    }
                }
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
                            location.getLocationProvider()?.apply {
                                Log.d("OCD", "Got location provider")
                                registerLocationConsumer(viewmodel)
                            } ?: Log.w("Location", "Couldn't get location provider")
                        } else {
                            location.enabled = false
                        }
                    }
                }
                MapEffect(state.geofenceFeature) { mapView ->
                    mapView.mapboxMap.style?.let {
                        updateGeofence(it, state)
                    } ?: Log.w("OCD", "Couldn't get mapbox style")
                }
            }
        }
    }
}

private fun updateGeofence(style: Style, state: AppState) =
    style.getSourceAs<GeoJsonSource>("source-geofence")?.let {
        it.updateGeoJSONSourceFeatures(listOf(state.geofenceFeature))
        Log.d("OCD", "Updated geofence feature ${state.geofenceFeature}")
    } ?: Log.w("Mapbox", "geofence source doesn't exist!")