import ActivityKit
import Combine
import Foundation

final class ActivityManager: ObservableObject {
    @MainActor @Published private(set) var activityID: String?
    
    static let shared = ActivityManager()
    
    func start() async {
        await stop()
        await startNewLiveActivity()
    }
    
    private func startNewLiveActivity() async {
        let attributes = LocationAlarmWidgetAttributes()
        let initialContentState = ActivityContent(
            state: LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: nil),
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
    
    func updateActivityRandomly() async {
        guard let activityID = await activityID,
              let runningActivity = Activity<LocationAlarmWidgetAttributes>.activities.first(where: { $0.id == activityID }) else {
            logger.warning("Activity to update isn't running")
            return
        }
        await runningActivity.update(using: LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: nil))
    }
    
    func stop() async {
        guard let activityID = await activityID,
              let runningActivity = Activity<LocationAlarmWidgetAttributes>.activities.first(where: { $0.id == activityID }) else {
            return
        }
        let initialContentState = LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "todo")

        await runningActivity.end(
            ActivityContent(state: initialContentState, staleDate: Date.distantFuture),
            dismissalPolicy: .immediate
        )
        
        await MainActor.run {
            self.activityID = nil
        }
    }
    
    func cancelAllRunningActivities() async {
        for activity in Activity<LocationAlarmWidgetAttributes>.activities {
            let initialContentState = LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: nil)
            
            await activity.end(
                ActivityContent(state: initialContentState, staleDate: Date()),
                dismissalPolicy: .immediate
            )
        }
        
        await MainActor.run {
            activityID = nil
        }
    }
    
}
