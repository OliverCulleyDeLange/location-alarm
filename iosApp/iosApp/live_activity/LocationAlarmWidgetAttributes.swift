import ActivityKit
import Combine
import Foundation

/// Data passed into the Live Activity to update its UI
struct LocationAlarmWidgetAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var distanceToAlarm: String?
        var alarmTriggered: Bool
    }
}
