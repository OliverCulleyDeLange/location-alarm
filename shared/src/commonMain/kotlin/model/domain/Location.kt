package model.domain

data class Location(
    val lat: Double,
    val lng: Double
) {
    fun distanceTo(target: Location) = haversineDistance(this, target)
}
