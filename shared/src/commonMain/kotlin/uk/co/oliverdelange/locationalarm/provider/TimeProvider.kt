package uk.co.oliverdelange.locationalarm.provider

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface TimeProvider {
    fun now(): Instant
}

class SystemTimeProvider : TimeProvider {
    override fun now() = Clock.System.now()
}