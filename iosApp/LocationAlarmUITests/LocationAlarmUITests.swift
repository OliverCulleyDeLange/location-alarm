import XCTest
import Shared

final class LocationAlarmUITests: XCTestCase {
    
    let app = XCUIApplication()
    let springboard = XCUIApplication(bundleIdentifier: "com.apple.springboard")
    let spotlight = XCUIApplication(bundleIdentifier: "com.apple.Spotlight")
    
    func waitAndTap(_ element: XCUIElement) {
        XCTAssert(element.waitForExistence(timeout: 2), "element didn't appear: \(element.debugDescription)")
        element.tap()
    }
    
    override func setUpWithError() throws {
        continueAfterFailure = false
        XCUIDevice.shared.press(.home)
        let appIcon = springboard.icons["Location Alarm"]
        if (appIcon.wait(for: \.isHittable, toEqual: true, timeout: 3)){
            appIcon.press(forDuration: 1)
            waitAndTap(springboard.buttons["Remove App"])
            waitAndTap(springboard.buttons["Delete App"])
            waitAndTap(springboard.buttons["Delete"])
        } else {
            waitAndTap(springboard.staticTexts["Search"])
            spotlight.typeText("Location Alarm")
            let icon = spotlight.icons["Location Alarm"]
            if (icon.waitForExistence(timeout: 3)){
                icon.press(forDuration: 1)
                waitAndTap(spotlight.buttons["Delete App"])
                waitAndTap(springboard.buttons["Delete"])
            }
        }
        app.resetAuthorizationStatus(for: .location)
        app.launchArguments = ["-UITests"]
        app.launch()
    }
    
    override func tearDownWithError() throws {
        
    }
    
    @MainActor
    func testEnableAndDisableAlarm() throws {
        XCTAssert(app.staticTexts[Shared.MapScreenStrings.shared.locationPermissionRequiredText]
            .waitForExistence(timeout: 1))
        waitAndTap(app.buttons[Shared.MapScreenStrings.shared.allowLocationAccess])
        waitAndTap(springboard.buttons["Allow While Using App"])
        let enableButton = app.buttons[Shared.MapScreenStrings.shared.enableAlarm]
        XCTAssert(enableButton.wait(for: \.exists, toEqual: true, timeout: 5))
        waitAndTap(enableButton)
        waitAndTap(springboard.buttons["Allow"])
        waitAndTap(app.buttons["Stop Alarm"])
    }
    
    @MainActor
    func testAllowOnceLocationPermissions() throws {
        waitAndTap(app.buttons[Shared.MapScreenStrings.shared.allowLocationAccess])
        waitAndTap(springboard.buttons["Allow Once"])
        let enableButton = app.buttons[Shared.MapScreenStrings.shared.enableAlarm]
        XCTAssert(enableButton.wait(for: \.exists, toEqual: true, timeout: 3))
        app.terminate()
        app.launch()
        XCTAssert(app.staticTexts[Shared.MapScreenStrings.shared.locationPermissionRequiredText]
            .waitForExistence(timeout: 1))
    }
    
    @MainActor
    func testDontAllowLocationPermissions() throws {
        waitAndTap(app.buttons[Shared.MapScreenStrings.shared.allowLocationAccess])
        waitAndTap(springboard.buttons["Don\u{2019}t Allow"])
        let deniedMsg = app.staticTexts[Shared.MapScreenStrings.shared.locationPermissionDeniedText]
        XCTAssert(deniedMsg.waitForExistence(timeout: 1))
        app.terminate()
        app.launch()
        XCTAssert(deniedMsg.waitForExistence(timeout: 1))
    }
    
    @MainActor
    func _testLaunchPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                XCUIApplication().launch()
            }
        }
    }
}

