import ActivityKit
import Combine
import Foundation

struct LocationAlarmWidgetAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var distanceToAlarm: String?
    }
}
