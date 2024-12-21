package uk.co.oliverdelange.locationalarm.model.domain

//@Serializable
data class Location(
    val lat: Double,
    val lng: Double
) {
    fun distanceTo(target: Location) =
        haversineDistance(this, target)
}
