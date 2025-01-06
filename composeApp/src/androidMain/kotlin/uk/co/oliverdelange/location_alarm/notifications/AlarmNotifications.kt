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

/** Builds the foreground notification that displays when the alarm is active */
fun buildPersistentAlarmNotification(context: Context): Notification {
    return Notification.Builder(context, NOTIFICATION_CHANNEL_ID_MAIN)
        .setSmallIcon(R.drawable.ic_location_alarm)
        .setContentTitle("Location Alarm")
        .setContentText("Location alarm is enabled")
        .setContentIntent(mainActivityIntent(context))
        .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .build()
}

/** Builds the foreground notification that displays when the alarm is active.
 * It displays distance until alarm sounds.
 * When alarm sounds, it turns red and gives the option to stop the alarm
 * */
fun buildTriggeredAlarmNotification(context: Context): Notification {
    val stopAlarmIntent = PendingIntent.getForegroundService(
        context,
        0,
        Intent(context, LocationAlarmService::class.java).apply {
            action = ACTION_STOP_AND_CANCEL_ALARM
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    return Notification.Builder(context, NOTIFICATION_CHANNEL_ID_MAIN)
        .setSmallIcon(R.drawable.ic_location_alarm)
        .setContentTitle("Location Alarm Triggered")
        .setContentText("You have reached your destination")
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setFullScreenIntent(mainActivityIntent(context), true)
        .addAction(Notification.Action.Builder(android.R.drawable.ic_media_pause, "Stop", stopAlarmIntent).build())
        .setColor(errorLight.toArgb())
        .setColorized(true)
        .build()
}

/** Builds the notification that displays distance until alarm sounds.
 * */
fun buildDistanceToAlarmNotification(context: Context, distanceMeters: Int): Notification {
    return Notification.Builder(context, NOTIFICATION_CHANNEL_ID_MAIN)
        .setSmallIcon(R.drawable.ic_location_alarm)
        .setContentTitle("Location Alarm Active")
        .setContentText("Alarm will sound in ${distanceMeters}m")
        .setContentIntent(mainActivityIntent(context))
        .setOnlyAlertOnce(true)
        .setCategory(NotificationCompat.CATEGORY_PROGRESS)
        .build()
}


fun mainActivityIntent(context: Context): PendingIntent? = PendingIntent.getActivity(
    context,
    0,
    Intent(context, MainActivity::class.java),
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)