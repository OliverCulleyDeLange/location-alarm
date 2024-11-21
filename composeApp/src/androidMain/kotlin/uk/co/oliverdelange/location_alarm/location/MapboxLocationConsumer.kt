package uk.co.oliverdelange.location_alarm.location

import android.animation.ValueAnimator
import android.util.Log
import com.mapbox.common.location.LocationError
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer

class MapboxLocationConsumer : LocationConsumer {
    // Location.kt Consumer overrides
    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    override fun onLocationUpdated(vararg location: Point, options: (ValueAnimator.() -> Unit)?) {
        Log.d("OCD", "Location.kt updated $location")
//        eventHandler.handle(LocationEvents.LocationChanged(location.map{ it.toLocation()}))
//        if (!_state.value.mapInteracted)
//            location.firstOrNull()?.let { point ->
//                _state.update {
//                    it.copy(
//                        geofenceFeature = buildGeofenceFeature(point, it.perimeterRadiusMeters)
//                    )
//                }
//            } ?: Log.w("Location.kt", "Location.kt update contains no location")
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