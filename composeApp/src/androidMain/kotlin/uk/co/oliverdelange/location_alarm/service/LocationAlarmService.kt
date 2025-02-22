package uk.co.oliverdelange.location_alarm.service

import android.Manifest
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
import uk.co.oliverdelange.location_alarm.R
import uk.co.oliverdelange.location_alarm.haptics.Vibrator
import uk.co.oliverdelange.location_alarm.notifications.buildDistanceToAlarmNotification
import uk.co.oliverdelange.location_alarm.notifications.buildPersistentAlarmNotification
import uk.co.oliverdelange.location_alarm.notifications.buildTriggeredAlarmNotification
import uk.co.oliverdelange.location_alarm.notifications.createAlarmNotificationChannel
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.store.AppStateStore

class LocationAlarmService : Service() {
    companion object {
        const val ACTION_STOP_AND_CANCEL_ALARM = "uk.co.oliverdelange.location_alarm.ACTION_STOP_AND_CANCEL_ALARM"
    }

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val persistentNotificationId = 60494
    private val triggeredNotificationId = 60495
    private val distanceToAlarmNotificationId = 60496
    private var alarmPlayer: MediaPlayer? = null
    private val appStateStore: AppStateStore = get()
    private val vibrator: Vibrator = get()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        SLog.d("LocationAlarmService onCreate")
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
                SLog.e("Media player error occurred: what=$what, extra=$extra")
                true
            }
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SLog.d("LocationAlarmService onStartCommand")
        when (intent?.action) {
            ACTION_STOP_AND_CANCEL_ALARM -> {
                appStateStore.onSetAlarm(false)
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
            persistentNotificationId,
            buildPersistentAlarmNotification(applicationContext),
            FOREGROUND_SERVICE_TYPE_LOCATION
        )

        serviceScope.launch {
            appStateStore.state.map { it.alarmTriggered }
                .distinctUntilChanged()
                .collect { alarmTriggered ->
                    if (alarmTriggered) {
                        alarmPlayer?.let {
                            if (!it.isPlaying) {
                                SLog.d("Triggering alarm sound & vibration")
                                it.start()
                            }
                        } ?: SLog.e("Alarm player is null")
                        vibrator.vibrateAlarm()
                    } else {
                        alarmPlayer?.let {
                            if (it.isPlaying) {
                                SLog.d("Stopping alarm sound & vibration")
                                it.stop()
                                it.prepare() //crash
                            }
                        } ?: SLog.e("Alarm player is null")
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
                            SLog.d("Updating persistent notification with new distance ($distanceToGeofencePerimeter) / triggered state ($alarmTriggered)")
                            if (alarmTriggered) {
                                notificationManager.notify(triggeredNotificationId, buildTriggeredAlarmNotification(applicationContext))
                                notificationManager.cancel(distanceToAlarmNotificationId)
                            } else {
                                notificationManager.notify(
                                    distanceToAlarmNotificationId,
                                    buildDistanceToAlarmNotification(applicationContext, distanceToGeofencePerimeter)
                                )
                                notificationManager.cancel(triggeredNotificationId)
                            }
                        } else {
                            SLog.w("Notification permissions aren't granted. Can't update notification")
                        }
                    }
                }
        }
    }

    override fun onDestroy() {
        vibrator.cancelVibration()
        serviceScope.cancel()
        notificationManager.cancel(persistentNotificationId)
        notificationManager.cancel(triggeredNotificationId)
        notificationManager.cancel(distanceToAlarmNotificationId)
        alarmPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
            }
        }
        alarmPlayer = null
        SLog.d("LocationAlarmService onDestroy")
        super.onDestroy()
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
}
