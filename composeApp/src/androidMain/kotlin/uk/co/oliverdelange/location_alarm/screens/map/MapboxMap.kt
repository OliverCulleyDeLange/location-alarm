package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.location.MapboxLocationConsumer
import uk.co.oliverdelange.location_alarm.mapper.domain_to_ui.toPoint
import uk.co.oliverdelange.location_alarm.mapper.ui_to_domain.toLocation
import uk.co.oliverdelange.locationalarm.mapbox.MapboxIDs

@Composable
@OptIn(MapboxExperimental::class)
fun MapboxMap(
    darkMap: Boolean,
    usersLocationToFlyTo: uk.co.oliverdelange.locationalarm.model.domain.Location?,
    locationPermissionStateGranted: Boolean,
    onMapTap: (uk.co.oliverdelange.locationalarm.model.domain.Location) -> Unit,
    onLocationUpdate: (List<uk.co.oliverdelange.locationalarm.model.domain.Location>) -> Unit,
    onFinishFlyingToUsersLocation: () -> Unit,
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
        compass = {
            Compass(
                alignment = Alignment.BottomStart,
                contentPadding = PaddingValues(start = 72.dp, bottom = 24.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .size(40.dp)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        },
        logo = { Logo(alignment = Alignment.TopStart, modifier = Modifier.statusBarsPadding()) },
        attribution = { Attribution(alignment = Alignment.TopEnd, modifier = Modifier.statusBarsPadding()) },
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
                onFinishFlyingToUsersLocation()
            }
        }
    }
}
