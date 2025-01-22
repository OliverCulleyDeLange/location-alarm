import XCTest

final class LocationAlarmUITests: XCTestCase {

    let app = XCUIApplication()
    let springboard = XCUIApplication(bundleIdentifier: "com.apple.springboard")

    override func setUpWithError() throws {
        continueAfterFailure = false
        app.resetAuthorizationStatus(for: .location)
        app.launchArguments = ["-UITests"]
        app.launch()
    }

    override func tearDownWithError() throws {

    }

    @MainActor
    func testEnableAndDisableAlarm() throws {
        app.buttons["Allow Location Access"].tap()
        springboard.buttons["Allow Once"].tap()
        let enableButton = app.buttons["Enable Alarm"]
        enableButton.wait(for: \.exists, toEqual: true, timeout: 3)
        enableButton.tap()
        Thread.sleep(forTimeInterval: 100)
        springboard.buttons["Allow"].tap()
        app.buttons["Stop Alarm"].tap()
    }

    @MainActor
    func testLaunchPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                XCUIApplication().launch()
            }
        }
    }
}
