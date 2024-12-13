import ActivityKit
import Combine
import Foundation




final class ActivityManager: ObservableObject {
    @MainActor @Published private(set) var activityID: String?
    @MainActor @Published private(set) var activityToken: String?
    
    static let shared = ActivityManager()
    
    func start() async {
        await stop()
        await startNewLiveActivity()
    }
    
    private func startNewLiveActivity() async {
        let attributes = LocationAlarmWidgetAttributes()
        let initialContentState = ActivityContent(
            state: LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "100m"),
            staleDate: nil
        )
        
        do {
            let activity = try Activity<LocationAlarmWidgetAttributes>.request(
                attributes: attributes,
                content: initialContentState,
                pushType: nil
            )
            logger.warning("Started live activity")
            
            await MainActor.run { activityID = activity.id }
            
            for await data in activity.pushTokenUpdates {
                let token = data.map {String(format: "%02x", $0)}.joined()
                logger.debug("LiveActivity token: \(token)")
                await MainActor.run { activityToken = token }
            }
        } catch {
            logger.warning("Couldn't start live activity \(error)")
        }
    }
    
    func updateActivityRandomly() async {
        guard let activityID = await activityID,
              let runningActivity = Activity<LocationAlarmWidgetAttributes>.activities.first(where: { $0.id == activityID }) else {
            return
        }
        let newRandomContentState = LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "todo")
        await runningActivity.update(using: newRandomContentState)
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
            self.activityToken = nil
        }
    }
    
    func cancelAllRunningActivities() async {
        for activity in Activity<LocationAlarmWidgetAttributes>.activities {
            let initialContentState = LocationAlarmWidgetAttributes.ContentState(distanceToAlarm: "TODO")
            
            await activity.end(
                ActivityContent(state: initialContentState, staleDate: Date()),
                dismissalPolicy: .immediate
            )
        }
        
        await MainActor.run {
            activityID = nil
            activityToken = nil
        }
    }
    
}
