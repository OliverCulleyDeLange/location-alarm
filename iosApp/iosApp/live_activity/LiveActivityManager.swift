import ActivityKit
import Combine
import Foundation
import UserNotifications

/// Handles creation and destruction of Live Activities (Widget used as a persistent notification)
final class LiveActivityManager: ObservableObject {
    @MainActor @Published private(set) var activityID: String?
    
    static let shared = LiveActivityManager()
    
    func start(newDistanceToAlarm: Int?, alarmTriggered: Bool) async {
        await stop()
        await startNewLiveActivity(newDistanceToAlarm, alarmTriggered)
    }
    
    private func startNewLiveActivity(_ newDistanceToAlarm: Int?, _ alarmTriggered: Bool) async {
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
            logger.debug("Started live activity")
            
            await MainActor.run { activityID = activity.id }
        } catch {
            logger.warning("Couldn't start live activity \(error)")
        }
    }
    
    func updateActivity(newDistanceToAlarm: Int, alarmTriggered: Bool) async {
        guard let activityID = await activityID,
              let runningActivity = Activity<LocationAlarmWidgetAttributes>.activities.first(where: { $0.id == activityID }) else {
            logger.warning("Activity to update isn't running")
            return
        }
        logger.debug("Updating live activity newDistanceToAlarm: \(newDistanceToAlarm), alarmTriggered: \(alarmTriggered)")
        await runningActivity.update(
            using: getContentStateFrom(newDistanceToAlarm, alarmTriggered)
        )
    }
    
    func stop() async {
        guard let activityID = await activityID,
              let runningActivity = Activity<LocationAlarmWidgetAttributes>.activities.first(
                where: { $0.id == activityID }
              ) else { return }
        let initialContentState = LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: nil, alarmTriggered: false)

        await runningActivity.end(
            ActivityContent(state: initialContentState, staleDate: Date.distantFuture),
            dismissalPolicy: .immediate
        )
        logger.debug("Stopped live activity")
        
        await MainActor.run {
            self.activityID = nil
        }
    }
    
    func cancelAllRunningActivities() async {
        for activity in Activity<LocationAlarmWidgetAttributes>.activities {
            let initialContentState = LocationAlarmWidgetAttributes.ContentState(
                distanceToAlarm: nil,
                alarmTriggered: false
            )
            
            await activity.end(
                ActivityContent(state: initialContentState, staleDate: Date()),
                dismissalPolicy: .immediate
            )
        }
        
        await MainActor.run {
            activityID = nil
        }
    }
    
    fileprivate func getContentStateFrom(_ newDistanceToAlarm: Int?, _ alarmTriggered: Bool) -> LocationAlarmWidgetAttributes.ContentState {
        return LocationAlarmWidgetAttributes.ContentState(
            distanceToAlarm: newDistanceToAlarm.map {_ in "\(String(newDistanceToAlarm!))m" },
            alarmTriggered: alarmTriggered
        )
    }
}
