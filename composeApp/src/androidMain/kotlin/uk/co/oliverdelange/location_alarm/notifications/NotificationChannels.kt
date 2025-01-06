package uk.co.oliverdelange.location_alarm.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri

val NOTIFICATION_CHANNEL_ID_MAIN = "LocationAlarmNotificationChannel"

fun createAlarmNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID_MAIN,
        "Location Alarm",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Required for the alarm to function"
        setSound(Uri.EMPTY, AudioAttributes.Builder().build())
    }
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}