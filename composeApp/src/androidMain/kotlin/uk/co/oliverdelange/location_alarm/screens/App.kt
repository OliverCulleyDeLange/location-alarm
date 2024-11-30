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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addGeoJSONSourceFeatures
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.extension.style.sources.removeGeoJSONSourceFeatures
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport
import mapbox.MapboxIDs.FEATURE_GEOFENCE
import mapbox.MapboxIDs.LAYER_GEOFENCE_FILL
import mapbox.MapboxIDs.LAYER_GEOFENCE_LINE
import mapbox.MapboxIDs.SOURCE_GEOFENCE
import model.domain.AppState
import model.domain.granted
import model.ui.AppViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.location.MapboxLocationConsumer
import uk.co.oliverdelange.location_alarm.mapbox.buildGeofenceFeature
import uk.co.oliverdelange.location_alarm.mapper.domain_to_ui.toPoint

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
                            +geoJsonSource(id = SOURCE_GEOFENCE)
                            +fillLayer(layerId = LAYER_GEOFENCE_FILL, sourceId = SOURCE_GEOFENCE) {
                                fillColor(color.toArgb())
                                fillOpacity(0.3)
                            }
                            +lineLayer(layerId = LAYER_GEOFENCE_LINE, sourceId = SOURCE_GEOFENCE) {
                                lineWidth(5.0)
                                lineColor(color.toArgb())
                            }
                        }
                    ) { style ->
                        Timber.d("Updating style after initialisation")
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
                                registerLocationConsumer(MapboxLocationConsumer {
                                    viewmodel.onLocationChange(it)
                                })
                                Timber.d("Registered location consumer")
                            } ?: Timber.w("Couldn't get location provider")
                        } else {
                            Timber.d("Location permission not granted, so location disabled")
                            location.enabled = false
                        }
                    }
                }
                MapEffect(state.geoFenceLocation) { mapView ->
                    mapView.mapboxMap.style?.let {
                        updateGeofence(it, state)
                        Timber.d("Updated geofence location ${state.geoFenceLocation}")
                    } ?: Timber.w("Couldn't get mapbox style")
                }
            }
        }
    }
}

private fun updateGeofence(style: Style, state: AppState) {
    state.geoFenceLocation?.toPoint()?.let {
        val geofenceFeature = buildGeofenceFeature(it, state.perimeterRadiusMeters)
        style.getSourceAs<GeoJsonSource>(SOURCE_GEOFENCE)?.let {
            it.removeGeoJSONSourceFeatures(listOf(FEATURE_GEOFENCE))
            it.addGeoJSONSourceFeatures(listOf(geofenceFeature))
            Timber.d("Updated geofence feature $geofenceFeature")
        } ?: Timber.w("geofence source doesn't exist!")
    } ?: Timber.d("Not updating geofence as no user location available")
}
