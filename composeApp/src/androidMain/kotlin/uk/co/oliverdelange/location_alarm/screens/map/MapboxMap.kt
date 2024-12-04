package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
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
import mapbox.MapboxIDs
import mapbox.MapboxIDs.FEATURE_GEOFENCE
import mapbox.MapboxIDs.SOURCE_GEOFENCE
import model.domain.Location
import timber.log.Timber
import toLocation
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.location.MapboxLocationConsumer
import uk.co.oliverdelange.location_alarm.mapbox.buildGeofenceFeature
import uk.co.oliverdelange.location_alarm.mapper.domain_to_ui.toPoint

@Composable
@OptIn(MapboxExperimental::class)
fun MapboxMap(
    perimeterRadiusMeters: Int,
    geoFenceLocation: Location?,
    locationPermissionStateGranted: Boolean,
    onMapTap: (Location) -> Unit,
    onLocationUpdate: (List<Location>) -> Unit,
) {
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
            onMapTap(point.toLocation())
            true
        },
    ) {
        val color = colorResource(R.color.geofenceBorder)
        MapEffect(Unit) { mapView ->
            mapView.mapboxMap.loadStyle(
                style(style = Style.LIGHT) {
                    +geoJsonSource(id = SOURCE_GEOFENCE)
                    +fillLayer(layerId = MapboxIDs.LAYER_GEOFENCE_FILL, sourceId = SOURCE_GEOFENCE) {
                        fillColor(color.toArgb())
                        fillOpacity(0.3)
                    }
                    +lineLayer(layerId = MapboxIDs.LAYER_GEOFENCE_LINE, sourceId = SOURCE_GEOFENCE) {
                        lineWidth(5.0)
                        lineColor(color.toArgb())
                    }
                }
            ) { style ->
                updateGeofence(style, geoFenceLocation, perimeterRadiusMeters)
            }
        }
        MapEffect(locationPermissionStateGranted) { mapView ->
            with<MapView, Unit>(mapView) {
                if (locationPermissionStateGranted) {
                    location.locationPuck = createDefault2DPuck(withBearing = false)
                    location.enabled = true
                    viewport.transitionTo(
                        targetState = viewport.makeFollowPuckViewportState(),
                        transition = viewport.makeImmediateViewportTransition()
                    )
                    location.getLocationProvider()?.apply {
                        registerLocationConsumer(MapboxLocationConsumer {
                            onLocationUpdate(it)
                        })
                        Timber.d("Registered location consumer")
                    } ?: Timber.w("Couldn't get location provider")
                } else {
                    Timber.d("Location permission not granted, so location disabled")
                    location.enabled = false
                }
            }
        }
        MapEffect(geoFenceLocation, perimeterRadiusMeters) { mapView ->
            mapView.mapboxMap.style?.let {
                updateGeofence(it, geoFenceLocation, perimeterRadiusMeters)
            } ?: Timber.w("Couldn't get mapbox style")
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
