import ActivityKit
import Combine
import Foundation
import UserNotifications

/// Handles creation and destruction of Live Activities (Widget used as a persistent notification) on IOS 16 and above 
@available(iOS 16.2, *)
final class LiveActivityManager: ObservableObject {
    
    @MainActor @Published private(set) var activityID: String?
    
    static let shared = LiveActivityManager()
    
    func createLiveActivity(_ newDistanceToAlarm: Int, _ alarmTriggered: Bool) async {
        await stopLiveActivity()
        await startNewLiveActivity(newDistanceToAlarm, alarmTriggered)
    }
    
    
    private func startNewLiveActivity(_ newDistanceToAlarm: Int, _ alarmTriggered: Bool) async {
        let attributes = LocationAlarmWidgetAttributes()
        let initialContentState = ActivityContent(
            state: getContentStateFrom(newDistanceToAlarm, alarmTriggered),
            staleDate: nil
        )
        
        do {
            let activity = try Activity<LocationAlarmWidgetAttributes>.request(
                attributes: attributes,
                content: initialContentState,
                pushType: nil
            )
            SLog.d("Started live activity")
            
            await MainActor.run { activityID = activity.id }
        } catch {
            SLog.w("Couldn't start live activity \(error)")
        }
    }
    
    func updatePersistentNotification(_ newDistanceToAlarm: Int, _ alarmTriggered: Bool) async {
        guard let activityID = await activityID,
              let runningActivity = Activity<LocationAlarmWidgetAttributes>.activities.first(where: { $0.id == activityID }) else {
            SLog.w("Activity to update isn't running")
            return
        }
        SLog.d("Updating live activity newDistanceToAlarm: \(newDistanceToAlarm), alarmTriggered: \(alarmTriggered)")
        await runningActivity.update(
            using: getContentStateFrom(newDistanceToAlarm, alarmTriggered)
        )
    }
    
    func stopLiveActivity() async {
        guard let activityID = await activityID,
              let runningActivity = Activity<LocationAlarmWidgetAttributes>.activities.first(
                where: { $0.id == activityID }
              ) else { return }
        let initialContentState = LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: nil, alarmTriggered: false)

        await runningActivity.end(
            ActivityContent(state: initialContentState, staleDate: Date.distantFuture),
            dismissalPolicy: .immediate
        )
        SLog.d("Stopped live activity")
        
        await MainActor.run {
            self.activityID = nil
        }
    }
    
    fileprivate func getContentStateFrom(_ newDistanceToAlarm: Int?, _ alarmTriggered: Bool) -> LocationAlarmWidgetAttributes.ContentState {
        return LocationAlarmWidgetAttributes.ContentState(
            distanceToAlarm: newDistanceToAlarm.map {_ in "\(String(newDistanceToAlarm!))m" },
            alarmTriggered: alarmTriggered
        )
    }
}
