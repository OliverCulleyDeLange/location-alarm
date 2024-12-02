package model.domain

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private val earthRadius = 6378137.0 // Earth's radius in meters

// Returns the haversine distance in meters from a to b
fun haversineDistance(source: Location, destination: Location): Double {
    val dLat = toRadians(destination.lat - source.lat)
    val dLng = toRadians(destination.lng - source.lng)

    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(toRadians(source.lat)) * cos(toRadians(destination.lat)) *
        sin(dLng / 2) * sin(dLng / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distance = earthRadius * c
    return distance
}