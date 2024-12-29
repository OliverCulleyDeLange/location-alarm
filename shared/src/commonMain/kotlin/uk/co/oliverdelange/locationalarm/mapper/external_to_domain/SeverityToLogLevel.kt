package uk.co.oliverdelange.locationalarm.mapper.external_to_domain

import co.touchlab.kermit.Severity
import uk.co.oliverdelange.locationalarm.model.domain.LogLevel

fun Severity.toLogLevel(): LogLevel {
    return when (this) {
        Severity.Verbose -> LogLevel.Verbose
        Severity.Debug -> LogLevel.Debug
        Severity.Info -> LogLevel.Info
        Severity.Warn -> LogLevel.Warn
        Severity.Error -> LogLevel.Error
        Severity.Assert -> LogLevel.Error
    }
}