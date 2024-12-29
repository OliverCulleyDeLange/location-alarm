package uk.co.oliverdelange.locationalarm.logging

import co.touchlab.kermit.Logger
import kotlin.jvm.JvmStatic

/** Shared Logging
 * Called Slog to not be confused with IOS' Log */
class SLog {
    companion object {
        init {
            Logger.setTag("LocationAlarm")
        }

        @JvmStatic
        fun e(m: String) = Logger.e(m)

        @JvmStatic
        fun w(m: String) = Logger.w(m)

        @JvmStatic
        fun i(m: String) = Logger.i(m)

        @JvmStatic
        fun d(m: String) = Logger.d(m)

        @JvmStatic
        fun v(m: String) = Logger.v(m)
    }
}
