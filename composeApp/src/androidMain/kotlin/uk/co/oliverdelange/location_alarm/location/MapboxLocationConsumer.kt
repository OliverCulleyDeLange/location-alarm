package uk.co.oliverdelange.location_alarm.location

import android.animation.ValueAnimator
import com.mapbox.common.location.LocationError
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import uk.co.oliverdelange.location_alarm.mapper.ui_to_domain.toLocation

/** Listens to locaiton updates from mapbox's inbuilt location service,
 * calls a callback with domain locations
 * //TODO Use normal location service - can't guarantee this will work when mapbox not shown!
 * */
class MapboxLocationConsumer(val onLocationChange: (List<uk.co.oliverdelange.locationalarm.model.domain.Location>) -> Unit) : LocationConsumer {
    override fun onLocationUpdated(vararg location: Point, options: (ValueAnimator.() -> Unit)?) {
        onLocationChange(location.map { it.toLocation() })
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