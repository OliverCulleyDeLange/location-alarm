package uk.co.oliverdelange.location_alarm.screens

import Greeting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState
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
import model.domain.Location
import model.domain.granted
import org.jetbrains.compose.ui.tooling.preview.Preview
import timber.log.Timber
import toLocation
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.location.MapboxLocationConsumer
import uk.co.oliverdelange.location_alarm.mapbox.buildGeofenceFeature
import uk.co.oliverdelange.location_alarm.mapper.domain_to_ui.toPoint

@OptIn(MapboxExperimental::class)
@Composable
@Preview
fun App(viewmodel: AppViewModel = viewModel()) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    val alarmButtonText by viewmodel.toggleAlarmButtonText.collectAsStateWithLifecycle("")

    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            val greeting = remember { Greeting().greet() }
            Text("Compose: $greeting")
            Text("Alarm: ${state.alarmTriggered}")
            Box {
                val mapState = rememberMapState()
                val mapViewportState = rememberMapViewportState {
                    setCameraOptions {
                        zoom(2.0)
                        center(Point.fromLngLat(-98.0, 39.5))
                        pitch(0.0)
                        bearing(0.0)
                    }
                }
                MapboxMap(
                    Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState,
                    mapState = mapState,
                    onMapClickListener = { point: Point ->
                        viewmodel.onMapTap(point.toLocation())
                        true
                    },
                ) {
                    Timber.w("OCD Map Init")
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
                            updateGeofence(style, state.geoFenceLocation, state.perimeterRadiusMeters)
                        }
                    }
                    MapEffect(state.locationPermissionState.granted()) { mapView ->
                        with<MapView, Unit>(mapView) {
                            if (state.locationPermissionState.granted()) {
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
                            updateGeofence(it, state.geoFenceLocation, state.perimeterRadiusMeters)
                        } ?: Timber.w("Couldn't get mapbox style")
                    }
                }
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onClick = { viewmodel.onToggleAlarm() },
                ) {
                    Text(alarmButtonText)
                }
            }
        }
    }
}

private fun updateGeofence(style: Style, location: Location?, perimeterRadiusMeters: Int) {
    location?.toPoint()?.let {
        val geofenceFeature = buildGeofenceFeature(it, perimeterRadiusMeters)
        style.getSourceAs<GeoJsonSource>(SOURCE_GEOFENCE)?.let {
            Timber.d("Updating geofence feature")
            it.removeGeoJSONSourceFeatures(listOf(FEATURE_GEOFENCE))
            it.addGeoJSONSourceFeatures(listOf(geofenceFeature))
            Timber.d("Updated geofence feature")
        } ?: Timber.w("geofence source doesn't exist!")
    } ?: Timber.d("Not updating geofence as no user location available")
}
