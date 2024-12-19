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
import android.os.Build
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
import uk.co.oliverdelange.location_alarm.haptics.Vibrator
import uk.co.oliverdelange.location_alarm.notifications.buildAlarmNotification
import uk.co.oliverdelange.location_alarm.notifications.createAlarmNotificationChannel
import uk.co.oliverdelange.locationalarm.model.domain.AppStateStore

class LocationAlarmService : Service() {
    companion object {
        const val ACTION_STOP_AND_CANCEL_ALARM = "uk.co.oliverdelange.location_alarm.ACTION_STOP_AND_CANCEL_ALARM"
        var isCreated: Boolean = false
        var isRunning: Boolean = false
    }

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val notificationId = 60494
    private var alarmPlayer: MediaPlayer? = null
    private val appStateStore: AppStateStore = get()
    private val vibrator: Vibrator = get()
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
            setOnErrorListener { _, what, extra ->
                Timber.e("Media player occurred: what=$what, extra=$extra")
                true
            }
        }
        super.onCreate()
        isCreated = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("LocationAlarmService onStartCommand")
        when (intent?.action) {
            ACTION_STOP_AND_CANCEL_ALARM -> {
                appStateStore.onSetAlarm(false)
            }

            else -> setupAlarm()
        }

        isRunning = true
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
                appStateStore.state.value.distanceToGeofencePerimeter,
                appStateStore.state.value.alarmTriggered
            ),
            FOREGROUND_SERVICE_TYPE_LOCATION
        )

        serviceScope.launch {
            appStateStore.state.map { it.alarmTriggered }
                .distinctUntilChanged()
                .collect { alarmTriggered ->
                    if (alarmTriggered) {
                        alarmPlayer?.let {
                            if (!it.isPlaying) {
                                Timber.d("Triggering alarm sound & vibration")
                                it.start()
                            }
                        } ?: Timber.e("Alarm player is null")
                        vibrator.vibrateAlarm()
                    } else {
                        alarmPlayer?.let {
                            if (it.isPlaying) {
                                Timber.d("Stopping alarm sound & vibration")
                                it.stop()
                                it.prepare()
                            }
                        } ?: Timber.e("Alarm player is null")
                        vibrator.cancelVibration()
                    }
                }
        }

        serviceScope.launch {
            appStateStore.state
                .map { it.distanceToGeofencePerimeter to it.alarmTriggered }
                .distinctUntilChanged()
                .collect { (distanceToGeofencePerimeter, alarmTriggered) ->
                    distanceToGeofencePerimeter?.let {
                        // Check the alarm is still enabled here as this service scope doesnt seem to get cancelled immediately
                        if (checkNotificationPermission() && appStateStore.state.value.alarmEnabled) {
                            Timber.d("Updating persistent notification with new distance ($distanceToGeofencePerimeter) / triggered state ($alarmTriggered)")
                            notificationManager.notify(notificationId, buildAlarmNotification(distanceToGeofencePerimeter, alarmTriggered))
                        } else {
                            Timber.w("Notification permissions aren't granted. Can't update notification")
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
        vibrator.cancelVibration()
        serviceScope.cancel()
        notificationManager.cancel(notificationId)
        Timber.d("LocationAlarmService onDestroy")
        super.onDestroy()
        isCreated = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // We're not allowing binding
    }

    private fun checkNotificationPermission(): Boolean {
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        return granted
    }

    private fun buildAlarmNotification(distanceToGeofencePerimeter: Int?, alarmTriggered: Boolean): Notification {
        val subtitle = when {
            alarmTriggered -> "You have reached your destination"
            else -> "Alarm will sound in ${distanceToGeofencePerimeter}m"
        }
        return buildAlarmNotification(applicationContext, "Location Alarm Active", subtitle, alarmTriggered)
    }
}
