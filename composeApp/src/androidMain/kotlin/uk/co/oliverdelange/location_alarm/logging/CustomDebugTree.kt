package uk.co.oliverdelange.location_alarm.logging

import timber.log.Timber

/** Simple Debug tree which appends a searchable tag to the debug tag for better logcat filtering */
class CustomDebugTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "LocationAlarm|${tag}", message, t)
    }
}