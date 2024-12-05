package uk.co.oliverdelange.location_alarm.service

import android.Manifest
import android.app.Notification
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
import uk.co.oliverdelange.location_alarm.notifications.buildAlarmNotification
import uk.co.oliverdelange.location_alarm.notifications.createAlarmNotificationChannel
import uk.co.oliverdelange.location_alarm.screens.AppViewModel

class LocationAlarmService : Service() {
    companion object {
        val ACTION_STOP_AND_CANCEL_ALARM = "uk.co.oliverdelange.location_alarm.ACTION_STOP_AND_CANCEL_ALARM"
    }

    private val notificationId = 60494
    private var alarmPlayer: MediaPlayer? = null
    private val viewModel: AppViewModel = get()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

            else -> setupAlarm()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /** Handles alarm notification and sound
     * - Ensures notification channel exists
     * - Makes this service a foreground service
     * - Listens for state updates to trigger alarm sound
     * - Updates notification based on new distance values and alarm triggered state
     * */
    private fun setupAlarm() {
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
    }

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

    override fun onBind(intent: Intent?): IBinder? {
        return null // We're not allowing binding
    }

    private fun checkPermission() = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun buildAlarmNotification(distanceToGeofencePerimeter: Int?, alarmTriggered: Boolean): Notification {
        val subtitle = when {
            alarmTriggered -> "You have reached your destination"
            else -> "Alarm will sound in ${distanceToGeofencePerimeter}m"
        }
        return buildAlarmNotification(applicationContext, "Location Alarm Active", subtitle, alarmTriggered)
    }
}
