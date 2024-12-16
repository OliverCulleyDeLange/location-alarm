package uk.co.oliverdelange.location_alarm.helpers

import uk.co.oliverdelange.location_alarm.BuildConfig

fun isDebug(): Boolean {
    return BuildConfig.DEBUG
}