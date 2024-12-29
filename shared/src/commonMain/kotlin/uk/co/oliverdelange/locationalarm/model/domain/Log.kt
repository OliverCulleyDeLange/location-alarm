package uk.co.oliverdelange.locationalarm.model.domain

import kotlinx.datetime.Instant

enum class LogLevel {
    Error, Warn, Info, Debug, Verbose
}

data class Log(
    val date: Instant,
    val level: LogLevel,
    val message: String,
)
