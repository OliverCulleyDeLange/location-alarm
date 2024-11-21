package uk.co.oliverdelange.location_alarm.mapbox

import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfTransformation.circle

fun buildGeofenceFeature(point: Point, perimeterRadiusMeters: Int): Feature = Feature.fromGeometry(
    circle(point, perimeterRadiusMeters.toDouble(), 360, TurfConstants.UNIT_METERS),
    null, "geofence-feature"
)