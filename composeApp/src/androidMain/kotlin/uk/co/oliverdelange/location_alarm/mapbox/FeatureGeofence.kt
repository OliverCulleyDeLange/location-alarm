package uk.co.oliverdelange.location_alarm.mapbox

import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfTransformation.circle
import uk.co.oliverdelange.locationalarm.mapbox.MapboxIDs.FEATURE_GEOFENCE

// We build a circle polygon geojson here instead of using a point with circle styling so we can accurately specify a radius in meters
fun buildGeofenceFeature(point: Point, perimeterRadiusMeters: Int): Feature = Feature.fromGeometry(
    circle(point, perimeterRadiusMeters.toDouble(), 360, TurfConstants.UNIT_METERS),
    null, FEATURE_GEOFENCE
)