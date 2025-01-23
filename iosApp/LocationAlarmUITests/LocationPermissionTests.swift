import XCTest
import Shared

final class LocationPermissionTests: BaseTest {
    override func setUpWithError() throws {
        setupFreshInstall()
    }
    
    @MainActor
    func testAllowOnceLocationPermissions() throws {
        waitAndTap(app.buttons[Shared.MapScreenStrings.shared.allowLocationAccess])
        allowOnce()
        let enableButton = app.buttons[Shared.MapScreenStrings.shared.enableAlarm]
        XCTAssert(enableButton.wait(for: \.exists, toEqual: true, timeout: 3))
        forceCloseAndReopen()
        XCTAssert(app.staticTexts[Shared.MapScreenStrings.shared.locationPermissionRequiredText]
            .waitForExistence(timeout: 1))
    }
    
    @MainActor
    func testDontAllowLocationPermissions() throws {
        waitAndTap(app.buttons[Shared.MapScreenStrings.shared.allowLocationAccess])
        dontAllow()
        let deniedMsg = app.staticTexts[Shared.MapScreenStrings.shared.locationPermissionDeniedText]
        XCTAssert(deniedMsg.waitForExistence(timeout: 1))
        forceCloseAndReopen()
        XCTAssert(deniedMsg.waitForExistence(timeout: 1))
    }

}

