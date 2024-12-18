package model.domain

import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.until

/** Whether the alarm should be allowed to trigger due to [MapFeatureState.delayAlarmTriggering] */
fun MapFeatureState.shouldDelayAlarm(): Boolean {
    return if (delayAlarmTriggering) {
        val now = Clock.System.now()
        alarmEnabledAt?.until(now, DateTimeUnit.SECOND)?.let {
            val delayAlarm = it < 5 // Delay alarm triggering by 5 seconds
            Logger.w("delayAlarm: $delayAlarm, seconds since alarmEnabled: $it,")
            delayAlarm
        } ?: false.also {
            Logger.w("Error computing duration between alarmEnabledAt ($alarmEnabledAt) and now ($now)")
        }
    } else false
}