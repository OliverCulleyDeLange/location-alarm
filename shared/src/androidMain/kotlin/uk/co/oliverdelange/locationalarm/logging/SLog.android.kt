package uk.co.oliverdelange.locationalarm.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.LogcatWriter

actual fun customLogWriter(): LogWriter {
    return LogcatWriter()
}