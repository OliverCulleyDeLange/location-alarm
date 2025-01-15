package uk.co.oliverdelange.location_alarm.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import uk.co.oliverdelange.location_alarm.MainActivity

@OptIn(ExperimentalTestApi::class)
class MapScreen(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    val disableButtonText = "DISABLE ALARM"
    val enableButtonText = "ENABLE ALARM"
    val stopAlarmDialogText = "Stop Alarm"
    val allowLocationPermissionButtonText = "Allow Location Access"
    val locationPermissionRequiredText =
        "This app needs your location to enable location based alarms. Please allow precise location access for the app to work."
    val locationPermissionDeniedText = "You have denied location permissions"

    // Text UI
    val locationPermissionRequiredTextNode = composeTestRule.onNode(hasText(locationPermissionRequiredText))
    val locationPermissionDeniedTextNode = composeTestRule.onNode(hasText(locationPermissionDeniedText))

    // Buttons
    val allowLocationPermissionButton = composeTestRule.onNode(hasText(allowLocationPermissionButtonText))
    val enableButton = composeTestRule.onNode(hasText(enableButtonText))
    val disableButton = composeTestRule.onNode(hasText(disableButtonText))
    val stopAlarmDialogButton = composeTestRule.onNode(hasText(stopAlarmDialogText))

    fun enableAlarm() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntilExactlyOneExists(hasText(enableButtonText).and(isEnabled()), 5000)
        enableButton.performClick()
    }

    fun disableAlarm() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntilExactlyOneExists(hasText(disableButtonText).and(isEnabled()), 5000)
        disableButton.performClick()
    }

    fun disableAlarmViaDialog() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntilExactlyOneExists(hasText(stopAlarmDialogText).and(isEnabled()), 5000)
        stopAlarmDialogButton.performClick()
    }
}