import XCTest
import Shared

final class NotificationPermissionTests: BaseTest {
    
    override func setUpWithError() throws {
        setupFreshInstall()
    }
    
    /// Tests location and notification permissions happy path
    @MainActor
    func testEnableAndDisableAlarm() throws {
        XCTAssert(app.staticTexts[Shared.MapScreenStrings.shared.locationPermissionRequiredText]
            .waitForExistence(timeout: 1))
        waitAndTap(app.buttons[Shared.MapScreenStrings.shared.allowLocationAccess])
        allowWhileUsingApp()
        let enableButton = app.buttons[Shared.MapScreenStrings.shared.enableAlarm]
        XCTAssert(enableButton.wait(for: \.exists, toEqual: true, timeout: 5))
        waitAndTap(enableButton)
        allow()
        waitAndTap(app.buttons["Stop Alarm"])
    }
    
    @MainActor
    func testDontAllowNotificationPermissions() throws {
        waitAndTap(app.buttons[Shared.MapScreenStrings.shared.allowLocationAccess])
        allowWhileUsingApp()
        waitAndTap(app.buttons[Shared.MapScreenStrings.shared.enableAlarm])
        dontAllow()
        XCTAssert(app.staticTexts[Shared.MapScreenStrings.shared.notificationPermissionDeniedText].waitForExistence(timeout: 1))
        let enableButton = app.buttons[Shared.MapScreenStrings.shared.enableAlarm]
        XCTAssert(enableButton.wait(for: \.exists, toEqual: true, timeout: 5))
        XCTAssert(enableButton.wait(for: \.isEnabled, toEqual: false, timeout: 5))
        forceCloseAndReopen()
        XCTAssert(app.staticTexts[Shared.MapScreenStrings.shared.notificationPermissionDeniedText].waitForExistence(timeout: 1))
        XCTAssert(enableButton.wait(for: \.isEnabled, toEqual: false, timeout: 5))
    }
}
