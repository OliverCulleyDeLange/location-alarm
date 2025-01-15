package uk.co.oliverdelange.location_alarm.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import uk.co.oliverdelange.location_alarm.MainActivity
import uk.co.oliverdelange.location_alarm.helpers.TestPermissionHelper
import uk.co.oliverdelange.location_alarm.screens.MapScreen

class LocationPermissionDeniedTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val mapScreen = MapScreen(composeTestRule)
    private val permissions = TestPermissionHelper()

    @Test
    fun denyLocationPermissions() {
        composeTestRule.waitForIdle()
        mapScreen.locationPermissionRequiredTextNode
            .assertExists()
        mapScreen.allowLocationPermissionButton
            .assertExists()
            .performClick()
        composeTestRule.waitForIdle()
        permissions.deny()
        mapScreen.locationPermissionDeniedTextNode
            .assertExists()
    }
}