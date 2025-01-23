import XCTest
import Shared

class BaseTest: XCTestCase{
    let app = XCUIApplication()
    let springboard = XCUIApplication(bundleIdentifier: "com.apple.springboard")
    let spotlight = XCUIApplication(bundleIdentifier: "com.apple.Spotlight")
    
    func waitAndTap(_ element: XCUIElement) {
        XCTAssert(element.waitForExistence(timeout: 2), "element didn't appear: \(element.debugDescription)")
        element.tap()
    }
    
    func forceCloseAndReopen() {
        app.terminate()
        app.launch()
    }
    
    func reinstallApp() {
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
    }
    
    func setupFreshInstall() {
        continueAfterFailure = false
        reinstallApp()
        app.launchArguments = ["-UITests"]
        app.launch()
    }
    
    /// Location permissions
    func allowWhileUsingApp() {
        waitAndTap(springboard.buttons["Allow While Using App"])
    }
    
    /// Location permissions
    func allowOnce() {
        waitAndTap(springboard.buttons["Allow Once"])
    }
    
    /// Notification permissions
    func allow() {
        waitAndTap(springboard.buttons["Allow"])
    }
    
    /// Notification and location
    func dontAllow() {
        waitAndTap(springboard.buttons["Don\u{2019}t Allow"])
    }
}
