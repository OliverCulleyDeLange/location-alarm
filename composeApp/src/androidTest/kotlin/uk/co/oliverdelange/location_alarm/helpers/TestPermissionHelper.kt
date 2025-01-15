package uk.co.oliverdelange.location_alarm.helpers

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

class TestPermissionHelper {
    val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    fun allow() {
        uiDevice
            .findObject(UiSelector().text("Allow"))
            .click()
        uiDevice.waitForIdle()
    }

    fun allowOnlyThisTime() {
        uiDevice
            .findObject(UiSelector().text("Only this time"))
            .click()
        uiDevice.waitForIdle()
    }

    fun allowWhileUsingApp() {
        uiDevice
            .findObject(UiSelector().text("While using the app"))
            .click()
        uiDevice.waitForIdle()
    }

    fun deny() {
        uiDevice
            // Couldn't figure out how to match the apostrophe so using regex with . any matcher here
            .findObject(UiSelector().textMatches("Don.t allow"))
            .click()
        uiDevice.waitForIdle()
    }
}