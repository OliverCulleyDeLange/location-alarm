package uk.co.oliverdelange.locationalarm.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import uk.co.oliverdelange.locationalarm.mapper.external_to_domain.toLogLevel
import uk.co.oliverdelange.locationalarm.provider.TimeProvider
import uk.co.oliverdelange.locationalarm.store.DebugStateStore

class StoringLogger(
    val debugStateStore: DebugStateStore,
    val timeProvider: TimeProvider,
) : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val date = timeProvider.now()
        debugStateStore.onLog(date, severity.toLogLevel(), message)
    }
}