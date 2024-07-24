package uk.co.oliverdelange.location_alarm

import android.animation.ValueAnimator
import android.util.Log
import androidx.lifecycle.ViewModel
import com.mapbox.common.location.LocationError
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfTransformation.circle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AppState(
    val locationPermissionState: PermissionState = PermissionState.Unknown,
    val geofenceFeature: Feature = buildGeofenceFeature(Point.fromLngLat(0.0, 0.0), 1000),
    val mapInteracted: Boolean = false,
    val perimeterRadiusMeters: Int = 500
)

fun buildGeofenceFeature(point: Point, perimeterRadiusMeters: Int): Feature = Feature.fromGeometry(
    circle(point, perimeterRadiusMeters.toDouble(), 360, TurfConstants.UNIT_METERS),
    null, "geofence-feature"
)


class AppViewModel : ViewModel(), LocationConsumer {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    fun onPermissionResult(granted: Boolean) {
        _state.update { current ->
            current.copy(
                locationPermissionState = if (granted) PermissionState.Granted else PermissionState.Denied
            )
        }
    }

    // Location Consumer overrides
    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    override fun onLocationUpdated(vararg location: Point, options: (ValueAnimator.() -> Unit)?) {
        Log.d("OCD", "Location updated $location")
        if (!_state.value.mapInteracted)
            location.firstOrNull()?.let { point ->
                _state.update {
                    it.copy(
                        geofenceFeature = buildGeofenceFeature(point, it.perimeterRadiusMeters)
                    )
                }
            } ?: Log.w("Location", "Location update contains no location")
    }

    override fun onBearingUpdated(vararg bearing: Double, options: (ValueAnimator.() -> Unit)?) {
    }

    override fun onError(error: LocationError) {
    }

    override fun onHorizontalAccuracyRadiusUpdated(vararg radius: Double, options: (ValueAnimator.() -> Unit)?) {
    }

    override fun onPuckAccuracyRadiusAnimatorDefaultOptionsUpdated(options: ValueAnimator.() -> Unit) {
    }

    override fun onPuckBearingAnimatorDefaultOptionsUpdated(options: ValueAnimator.() -> Unit) {
    }

    override fun onPuckLocationAnimatorDefaultOptionsUpdated(options: ValueAnimator.() -> Unit) {
    }
}
