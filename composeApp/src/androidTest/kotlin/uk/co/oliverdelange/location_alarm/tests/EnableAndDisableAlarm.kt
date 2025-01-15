package uk.co.oliverdelange.location_alarm.tests

import android.os.Build
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.GlobalContext.get
import uk.co.oliverdelange.location_alarm.MainActivity
import uk.co.oliverdelange.location_alarm.helpers.TestPermissionHelper
import uk.co.oliverdelange.location_alarm.location.LocationService
import uk.co.oliverdelange.location_alarm.screens.MapScreen
import uk.co.oliverdelange.locationalarm.model.domain.Location

class EnableAndDisableAlarm {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val mapScreen = MapScreen(composeTestRule)
    private val permissions = TestPermissionHelper()

    private val fakeLocation = Location(lat = 53.0, lng = -1.0)
    @Test
    fun enableAndDisableAlarm() {
        val fakeLocationService = get().get<LocationService>()
        composeTestRule.waitForIdle()
        mapScreen.locationPermissionRequiredTextNode
            .assertExists()
        mapScreen.allowLocationPermissionButton
            .assertExists()
            .performClick()
        composeTestRule.waitForIdle()
        permissions.allowWhileUsingApp()
        fakeLocationService.onLocationUpdate(fakeLocation)
        mapScreen.enableAlarm()
        // Notification permissions added in Tiramisu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.allow()
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(mapScreen.disableButtonText)
            .assertExists()

        mapScreen.disableAlarmViaDialog()
    }
}