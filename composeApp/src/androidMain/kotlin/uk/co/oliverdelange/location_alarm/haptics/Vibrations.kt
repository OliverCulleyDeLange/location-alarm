package uk.co.oliverdelange.location_alarm.haptics

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager

class Vibrator(context: Context) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

    fun vibrateAlarm() {
        val vibration = VibrationEffect.createWaveform(longArrayOf(0, 500, 250, 500), intArrayOf(0, 100, 0, 255), 0)
        vibratorManager.defaultVibrator.vibrate(vibration)
    }

    fun cancelVibration() = vibratorManager.cancel()
}
