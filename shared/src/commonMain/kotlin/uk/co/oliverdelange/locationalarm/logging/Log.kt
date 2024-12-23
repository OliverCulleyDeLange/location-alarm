package uk.co.oliverdelange.locationalarm.logging

import co.touchlab.kermit.Logger

object Log {
    fun e(m: String) = Logger.e(m)
    fun w(m: String) = Logger.w(m)
    fun i(m: String) = Logger.i(m)
    fun d(m: String) = Logger.d(m)
    fun v(m: String) = Logger.v(m)
}