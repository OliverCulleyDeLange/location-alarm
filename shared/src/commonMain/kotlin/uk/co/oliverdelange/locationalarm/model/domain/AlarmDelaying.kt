package uk.co.oliverdelange.locationalarm.model.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.until
import uk.co.oliverdelange.locationalarm.logging.SLog

/* Dev tool to delay the alarm triggering for 5 seconds to allow testing at desk
* TODO Extract tools like this into a seperate state store not linked to a view */

/** Whether to delay alarm triggering by a hardcoded 5 seconds.
 * This is a dumb hack to manually test in a situation where you don't have reliable GPS (like indoors at your desk)
 * TODO this shouldn't really be in prod code - how to extract it out into debug only code
 * TODO Its also a nasty global var
 * */
var delayAlarmTriggering: Boolean = false

/** Whether the alarm should be allowed to trigger due to [AppState.delayAlarmTriggering]
 * Delays alarm if the alarmStartTime is less than 5 seconds in the past
 * */
fun AppState.shouldDelayAlarm(): Boolean {
    return if (delayAlarmTriggering) {
        val now = Clock.System.now()
        alarmEnabledAt?.until(now, DateTimeUnit.SECOND)?.let {
            val delayAlarm = it < 5 // Delay alarm triggering by 5 seconds
            SLog.w("shouldDelayAlarm::: delayAlarm: $delayAlarm, seconds since alarmEnabled: $it,")
            delayAlarm
        } ?: false.also {
            SLog.w("Error computing duration between alarmEnabledAt ($alarmEnabledAt) and now ($now)")
        }
    } else false
}