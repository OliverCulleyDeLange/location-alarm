import ActivityKit
import Combine
import Foundation

struct LocationAlarmWidgetAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var distanceToAlarm: String
    }
}
