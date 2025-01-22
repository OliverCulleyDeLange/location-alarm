package uk.co.oliverdelange.locationalarm.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.mutableLoggerConfigInit
import kotlin.jvm.JvmStatic

expect fun customLogWriter(): LogWriter

/** Shared Logging
 * Called Slog to not be confused with IOS' Log */
object SLog {
    private val logger = Logger(
        config = mutableLoggerConfigInit(
            customLogWriter(),
            minSeverity = Severity.Verbose
        ),
        tag = ":LocationAlarm:"
    )

    fun setLogWriters(vararg logWriter: LogWriter) {
        logger.mutableConfig.logWriterList = logWriter.toList()
    }

    @JvmStatic
    fun e(m: String) = logger.e(m)

    @JvmStatic
    fun w(m: String) = logger.w(m)

    @JvmStatic
    fun i(m: String) = logger.i(m)

    @JvmStatic
    fun d(m: String) = logger.d(m)

    @JvmStatic
    fun v(m: String) = logger.v(m)

    /** Purely used for temporary debug logs so we can find them easily and remove */
    @JvmStatic
    fun dbg(m: String) = logger.e(m)
}
