package uk.co.oliverdelange.location_alarm.helpers

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

class TestNotificationsHelper {
    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    fun alarmTriggeredNotification() = uiDevice.findObject(UiSelector().text("Location Alarm Triggered"))
    fun stopButton() = uiDevice.findObject(UiSelector().text("Stop"))

    fun tapStopIfExists() {
        val stopButton = stopButton()
        if (stopButton.waitForExists(3000)) {
            stopButton.click()
        } else {
            println("Stop button doesn't exist to click")
        }
    }

}