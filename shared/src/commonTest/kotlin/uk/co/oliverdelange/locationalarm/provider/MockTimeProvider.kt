package uk.co.oliverdelange.locationalarm.provider

import kotlinx.datetime.Instant

class MockTimeProvider : TimeProvider {
    var storedInstant = Instant.fromEpochSeconds(0)
    override fun now() = storedInstant
    fun set(epochSeconds: Long) {
        storedInstant = Instant.fromEpochSeconds(epochSeconds)
    }
}