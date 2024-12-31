package uk.co.oliverdelange.locationalarm.model.domain

import kotlinx.datetime.Instant

data class LocationUpdate(
    val date: Instant,
    val location: Location,
)
