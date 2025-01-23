import XCTest

final class LocationAlarmThemeTests: XCTestCase {

    let app = XCUIApplication()
    let springboard = XCUIApplication(bundleIdentifier: "com.apple.springboard")

    override class var runsForEachTargetApplicationUIConfiguration: Bool {
        true
    }

    override func setUpWithError() throws {
        continueAfterFailure = false
        app.resetAuthorizationStatus(for: .location)
        app.launch()
    }

    @MainActor
    func testScreenShotThemeVariations() throws {
        screenshot("Location Permission Screen", app)
        app.buttons["Allow Location Access"].tap()
        springboard.buttons["Allow Once"].tap()
        Thread.sleep(forTimeInterval: 1)
        screenshot("Map Screen", app)
    }
    
    func screenshot(_ name: String, _ app: XCUIApplication) {
        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
