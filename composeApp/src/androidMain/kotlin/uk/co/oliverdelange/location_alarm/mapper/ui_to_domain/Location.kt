package uk.co.oliverdelange.location_alarm.mapper.ui_to_domain

import com.mapbox.geojson.Point
import android.location.Location as AndroidLocation
import uk.co.oliverdelange.locationalarm.model.domain.Location as SharedLocation

fun Point.toLocation(): SharedLocation =
    SharedLocation(latitude(), longitude())

fun AndroidLocation.toLocation(): SharedLocation = SharedLocation(
    lat = latitude,
    lng = longitude
)