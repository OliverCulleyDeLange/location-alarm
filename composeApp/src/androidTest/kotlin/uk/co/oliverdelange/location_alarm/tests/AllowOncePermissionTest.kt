package uk.co.oliverdelange.location_alarm.tests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import uk.co.oliverdelange.location_alarm.MainActivity
import uk.co.oliverdelange.location_alarm.helpers.TestPermissionHelper
import uk.co.oliverdelange.location_alarm.screens.MapScreen

@OptIn(ExperimentalTestApi::class)
class AllowOncePermissionTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val mapScreen = MapScreen(composeTestRule)
    private val permissions = TestPermissionHelper()

    @Test
    fun allowOnceLocationPermissions() {
        composeTestRule.waitForIdle()
        mapScreen.locationPermissionRequiredTextNode.assertExists()
        mapScreen.allowLocationPermissionButton
            .assertExists()
            .performClick()
        composeTestRule.waitForIdle()
        permissions.allowOnlyThisTime()
        composeTestRule.waitUntilExactlyOneExists(hasText(mapScreen.enableButtonText), 3000)
    }
}