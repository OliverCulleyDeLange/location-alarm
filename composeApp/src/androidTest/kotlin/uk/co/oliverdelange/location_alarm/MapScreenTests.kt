package uk.co.oliverdelange.location_alarm

import android.os.Build
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MapScreenTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun enableAlarm() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Allow Location Access")
            .assertExists()
            .performClick()
        composeTestRule.waitForIdle()
        uiDevice
            .findObject(UiSelector().text("While using the app"))
            .click()
        uiDevice.waitForIdle()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntilExactlyOneExists(hasText("ENABLE ALARM").and(isEnabled()), 5000)
        composeTestRule.onNodeWithText("ENABLE ALARM")
            .assertIsEnabled()
            .performClick()
        // Notification permissions added in Tiramisu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            composeTestRule.waitForIdle()
            uiDevice.waitForIdle()
            uiDevice
                .findObject(UiSelector().text("Allow"))
                .click()
            composeTestRule.waitForIdle()
        }
        composeTestRule.onNodeWithText("DISABLE ALARM")
            .assertExists()
    }
}