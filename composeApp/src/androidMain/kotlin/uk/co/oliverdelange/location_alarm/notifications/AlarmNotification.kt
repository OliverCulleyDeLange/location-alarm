package uk.co.oliverdelange.location_alarm.notifications

import android.app.Notification
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import uk.co.oliverdelange.location_alarm.MainActivity
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.service.LocationAlarmService
import uk.co.oliverdelange.location_alarm.service.LocationAlarmService.Companion.ACTION_STOP_AND_CANCEL_ALARM
import uk.co.oliverdelange.location_alarm.ui.theme.errorLight

/** Builds the foreground notification that displays when the alarm is active.
 * It displays distance until alarm sounds.
 * When alarm sounds, it turns red and gives the option to stop the alarm
 * */
fun buildAlarmNotification(context: Context, title: String, subtitle: String, alarmTriggered: Boolean): Notification {
    return Notification.Builder(context, NOTIFICATION_CHANNEL_ID_MAIN)
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setContentTitle(title)
        .setContentText(subtitle)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        .setOngoing(true) // User can't dismiss notification
        .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
        .setOnlyAlertOnce(true)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setPriority(Notification.PRIORITY_HIGH)
        .run {
            if (alarmTriggered) {
                val stopAlarmIntent = PendingIntent.getForegroundService(
                    context,
                    0,
                    Intent(context, LocationAlarmService::class.java).apply {
                        action = ACTION_STOP_AND_CANCEL_ALARM
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                addAction(Notification.Action.Builder(android.R.drawable.ic_media_pause, "Stop", stopAlarmIntent).build())
                    .setColor(errorLight.toArgb())
                    .setColorized(true)
            } else this
        }
        .build()
}