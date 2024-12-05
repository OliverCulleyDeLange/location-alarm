package uk.co.oliverdelange.location_alarm.service

import android.Manifest
import android.app.Notification
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.PendingIntent
import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.notifications.NOTIFICATION_CHANNEL_ID_MAIN
import uk.co.oliverdelange.location_alarm.notifications.createAlarmNotificationChannel
import uk.co.oliverdelange.location_alarm.screens.AppViewModel
import uk.co.oliverdelange.location_alarm.ui.theme.errorLight

class LocationAlarmService : Service() {
    val ACTION_STOP_AND_CANCEL_ALARM = "uk.co.oliverdelange.location_alarm.ACTION_STOP_AND_CANCEL_ALARM"

    private val notificationId = 60494
    private var alarmPlayer: MediaPlayer? = null
    private val viewModel: AppViewModel = get()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? {
        return null // We're not allowing binding
    }

    override fun onCreate() {
        Timber.d("LocationAlarmService onCreate")
        val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(packageName)
                .appendPath("${R.raw.alarm}")
                .build()
        val alarmAudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        alarmPlayer = MediaPlayer().apply {
            setDataSource(this@LocationAlarmService, alarmUri)
            setAudioAttributes(alarmAudioAttributes)
            isLooping = true
            prepare()
            setOnErrorListener { mp, what, extra ->
                Timber.e("Media player occurred: what=$what, extra=$extra")
                true
            }
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("LocationAlarmService onStartCommand")
        when (intent?.action) {
            ACTION_STOP_AND_CANCEL_ALARM -> {
                viewModel.onSetAlarm(false)
            }
        }

        createAlarmNotificationChannel(this)
        ServiceCompat.startForeground(
            this,
            notificationId,
            buildAlarmNotification(
                viewModel.state.value.distanceToGeofencePerimeter,
                viewModel.state.value.alarmTriggered
            ),
            FOREGROUND_SERVICE_TYPE_LOCATION
        )

        serviceScope.launch {
            viewModel.state.map { it.alarmTriggered }.distinctUntilChanged().collect { alarmTriggered ->
                if (alarmTriggered) {
                    Timber.d("Triggering alarm sound")
                    alarmPlayer?.let {
                        if (!it.isPlaying) it.start()
                    } ?: Timber.e("Alarm player is null")
                } else {
                    Timber.d("Stopping alarm sound")
                    alarmPlayer?.let {
                        if (it.isPlaying) {
                            it.stop()
                            it.prepare()
                        }
                    } ?: Timber.e("Alarm player is null")
                }
            }
        }

        serviceScope.launch {
            viewModel.state
                .map { it.distanceToGeofencePerimeter to it.alarmTriggered }
                .distinctUntilChanged()
                .collect { (distanceToGeofencePerimeter, alarmTriggered) ->
                    distanceToGeofencePerimeter?.let {
                        if (checkPermission()) {
                            val notificationManager = NotificationManagerCompat.from(this@LocationAlarmService)
                            notificationManager.notify(notificationId, buildAlarmNotification(distanceToGeofencePerimeter, alarmTriggered))
                        }
                    }
                }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun checkPermission() = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        alarmPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
            }
        }
        alarmPlayer = null
        serviceScope.cancel()
        NotificationManagerCompat.from(this).cancel(notificationId)
        Timber.d("LocationAlarmService onDestroy")
        super.onDestroy()
    }

    private fun buildAlarmNotification(distanceToGeofencePerimeter: Int?, alarmTriggered: Boolean): Notification {
        val subtitle = when {
            alarmTriggered -> "You have reached your destination"
            else -> "Alarm will sound in ${distanceToGeofencePerimeter}m"
        }
        return buildNotification("Location Alarm Active", subtitle, alarmTriggered)
    }

    private fun buildNotification(title: String, subtitle: String, alarmTriggered: Boolean): Notification {
        return Notification.Builder(this, NOTIFICATION_CHANNEL_ID_MAIN)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setOngoing(true) // User can't dismiss notification
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(Notification.PRIORITY_HIGH)
            .run {
                if (alarmTriggered) {
                    val stopAlarmIntent = PendingIntent.getForegroundService(
                        applicationContext,
                        0,
                        Intent(applicationContext, LocationAlarmService::class.java).apply {
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
}
