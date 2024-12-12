package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.ColorValue
import com.mapbox.maps.extension.compose.style.DoubleValue
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.compose.style.layers.generated.FillLayer
import com.mapbox.maps.extension.compose.style.layers.generated.LineLayer
import com.mapbox.maps.extension.compose.style.sources.generated.GeoJsonSourceState
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.viewport
import mapbox.MapboxIDs
import model.domain.Location
import timber.log.Timber
import toLocation
import uk.co.oliverdelange.location_alarm.location.MapboxLocationConsumer
import uk.co.oliverdelange.location_alarm.mapper.domain_to_ui.toPoint

@Composable
@OptIn(MapboxExperimental::class)
fun MapboxMap(
    darkMap: Boolean,
    usersLocationToFlyTo: Location?,
    locationPermissionStateGranted: Boolean,
    onMapTap: (Location) -> Unit,
    onLocationUpdate: (List<Location>) -> Unit,
    geofenceSourceState: GeoJsonSourceState,
) {
    val mapState = rememberMapState {}
    val mapViewportState = rememberMapViewportState {}
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        mapState = mapState,
        onMapClickListener = { point: Point ->
            onMapTap(point.toLocation())
            true
        },
        scaleBar = {},
        style = { MapStyle(if (darkMap) Style.DARK else Style.LIGHT) }
    ) {
        val color = MaterialTheme.colorScheme.primary
        LineLayer(
            layerId = MapboxIDs.LAYER_GEOFENCE_LINE,
            sourceState = geofenceSourceState,
            lineColor = ColorValue(color),
            lineWidth = DoubleValue(5.0),
        )
        FillLayer(
            layerId = MapboxIDs.LAYER_GEOFENCE_FILL,
            sourceState = geofenceSourceState,
            fillColor = ColorValue(color),
            fillOpacity = DoubleValue(0.3)
        )
        MapEffect(locationPermissionStateGranted) { mapView ->
            with<MapView, Unit>(mapView) {
                if (locationPermissionStateGranted) {
                    location.locationPuck = createDefault2DPuck(withBearing = false)
                    location.enabled = true
                    viewport.transitionTo(
                        targetState = viewport.makeFollowPuckViewportState(
                            FollowPuckViewportStateOptions.Builder()
                                .pitch(0.0)
                                .zoom(14.0)
                                .build()
                        ),
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
        LaunchedEffect(usersLocationToFlyTo) {
            usersLocationToFlyTo?.let {
                val cameraOptions = CameraOptions.Builder()
                    .center(it.toPoint())
                    .zoom(16.0)
                    .build()
                mapViewportState.flyTo(cameraOptions)
            }
        }
    }
}
