package uk.co.oliverdelange.locationalarm.logging

import co.touchlab.kermit.DefaultFormatter
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Message
import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.OSLogWriter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag
import platform.darwin.os_log_type_t

actual fun customLogWriter(): LogWriter {
    return XcodeSeverityWriter()
}

/** Copied from the Kermit version to customise the underlying OSLogWriter*/
open class XcodeSeverityWriter(private val messageStringFormatter: MessageStringFormatter = DefaultFormatter) :
    OSLogWriter(messageStringFormatter, subsystem = "uk.co.oliverdelange.locationalarm", "ioslogs", true) {
    override fun formatMessage(severity: Severity, tag: Tag, message: Message): String =
        "${emojiPrefix(severity)} ${messageStringFormatter.formatMessage(null, tag, message)}"

    override fun logThrowable(osLogSeverity: os_log_type_t, throwable: Throwable) {
        // oslog cuts off longer strings, so for local development, println is more useful
        println(throwable.stackTraceToString())
    }

    //If this looks familiar, yes, it came directly from Napier :) https://github.com/AAkira/Napier#darwinios-macos-watchos-tvosintelapple-silicon
    open fun emojiPrefix(severity: Severity): String = when (severity) {
        Severity.Verbose -> "⚪️"
        Severity.Debug -> "🔵"
        Severity.Info -> "🟢"
        Severity.Warn -> "🟡"
        Severity.Error -> "🔴"
        Severity.Assert -> "🟤️"
    }
}