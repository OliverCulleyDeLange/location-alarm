package uk.co.oliverdelange.location_alarm.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import uk.co.oliverdelange.location_alarm.MainActivity
import uk.co.oliverdelange.locationalarm.strings.MapScreenStrings.allowLocationAccess
import uk.co.oliverdelange.locationalarm.strings.MapScreenStrings.disableAlarm
import uk.co.oliverdelange.locationalarm.strings.MapScreenStrings.enableAlarm
import uk.co.oliverdelange.locationalarm.strings.MapScreenStrings.locationPermissionDeniedText
import uk.co.oliverdelange.locationalarm.strings.MapScreenStrings.locationPermissionRequiredText
import uk.co.oliverdelange.locationalarm.strings.MapScreenStrings.stopAlarm

@OptIn(ExperimentalTestApi::class)
class MapScreen(private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
    // Text UI
    val locationPermissionRequiredTextNode = composeTestRule.onNode(hasText(locationPermissionRequiredText))
    val locationPermissionDeniedTextNode = composeTestRule.onNode(hasText(locationPermissionDeniedText))

    // Buttons
    val allowLocationPermissionButton = composeTestRule.onNode(hasText(allowLocationAccess))
    val enableButton = composeTestRule.onNode(hasText(enableAlarm))
    val disableButton = composeTestRule.onNode(hasText(disableAlarm))
    val stopAlarmDialogButton = composeTestRule.onNode(hasText(stopAlarm))

    fun enableAlarm() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntilExactlyOneExists(hasText(enableAlarm).and(isEnabled()), 5000)
        enableButton.performClick()
    }

    fun disableAlarm() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntilExactlyOneExists(hasText(disableAlarm).and(isEnabled()), 5000)
        disableButton.performClick()
    }

    fun disableAlarmViaDialog() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntilExactlyOneExists(hasText(stopAlarm).and(isEnabled()), 5000)
        stopAlarmDialogButton.performClick()
    }
}