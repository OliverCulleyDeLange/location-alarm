import XCTest
import Shared

final class LocationAlarmUITests: XCTestCase {
    
    let app = XCUIApplication()
    let springboard = XCUIApplication(bundleIdentifier: "com.apple.springboard")
    
    func waitAndTap(_ element: XCUIElement) {
        element.waitForExistence(timeout: 1)
        element.tap()
    }
    
    override func setUpWithError() throws {
        continueAfterFailure = false
        XCUIDevice.shared.press(.home)
        let appIcon = springboard.icons["Location Alarm"]
        if (appIcon.waitForExistence(timeout: 3)){
            appIcon.press(forDuration: 1)
            waitAndTap(springboard.buttons["Remove App"])
            waitAndTap(springboard.buttons["Delete App"])
            waitAndTap(springboard.buttons["Delete"])
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
        app.buttons[Shared.MapScreenStrings.shared.allowLocationAccess].tap()
        springboard.buttons["Allow Once"].tap()
        let enableButton = app.buttons[Shared.MapScreenStrings.shared.enableAlarm]
        XCTAssert(enableButton.wait(for: \.exists, toEqual: true, timeout: 3))
        enableButton.tap()
        waitAndTap(springboard.buttons["Allow"])
        waitAndTap(app.buttons["Stop Alarm"])
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
