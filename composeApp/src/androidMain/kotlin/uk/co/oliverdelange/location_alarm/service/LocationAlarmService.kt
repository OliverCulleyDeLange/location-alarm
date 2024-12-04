package uk.co.oliverdelange.location_alarm.service

import android.app.Notification
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.IBinder
import androidx.core.app.ServiceCompat
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.notifications.NOTIFICATION_CHANNEL_ID_MAIN
import uk.co.oliverdelange.location_alarm.notifications.createAlarmNotificationChannel

class LocationAlarmService : Service() {
    private val notificationId = 60494

    override fun onBind(intent: Intent?): IBinder? {
        return null // We're not allowing binding
    }

    override fun onCreate() {
        Timber.d("LocationAlarmService onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("LocationAlarmService onStartCommand")
        createAlarmNotificationChannel(this)
        val notification = Notification.Builder(this, NOTIFICATION_CHANNEL_ID_MAIN)
            .setSmallIcon(R.drawable.ic_notification_icon) // Replace with your icon
            .setContentTitle("Location Alarm Active")
            .setContentText("Alarm will sound near your destination")
            .setOngoing(true) // User can't dismiss notification
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .build()
        ServiceCompat.startForeground(
            this,
            notificationId,
            notification,
            FOREGROUND_SERVICE_TYPE_LOCATION
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Timber.d("LocationAlarmService onDestroy")
        super.onDestroy()
    }
}