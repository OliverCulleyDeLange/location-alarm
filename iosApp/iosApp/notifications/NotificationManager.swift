import UserNotifications
import Shared

/// Handles firing local notifications
class NotificationManager {
    
    static let shared = NotificationManager()
    
    private let notificationId = "LocationAlarmNotification"
    
    func createAlarmNotification() {
        SLog.d("createAlarmNotification")
        let content = UNMutableNotificationContent()
        content.title = "Location Alarm"
        content.body = "You have reached your destination"
        
        let request = UNNotificationRequest(
            identifier: notificationId,
            content: content,
            trigger: nil
        )
        
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                SLog.e("Error scheduling notification: \(error)")
            }
        }
    }
    
    
    func removeAlarmNotification() {
        UNUserNotificationCenter.current().removeDeliveredNotifications(withIdentifiers: [notificationId])
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [notificationId])
    }
    
    func requestPermissions( onNotificationPermissionResult: @escaping (Bool) -> Void) {
        Task {
            let center = UNUserNotificationCenter.current()
            do {
                let granted = try await center.requestAuthorization(options: [.alert, .sound, .badge])
                SLog.d("Notification permissions granted: \(granted)")
                await MainActor.run {
                    onNotificationPermissionResult(granted)
                }
            } catch {
                SLog.d("Error requesting notification permissions: \(error)")
            }
        }
    }
    
    func checkPermissions(onNotificationPermissionResult: @escaping (PermissionState) -> Void) {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            DispatchQueue.main.async {
                let state: PermissionState = {switch(settings.authorizationStatus) {
                case .authorized, .ephemeral:
                    return PermissionStateGranted()
                case .denied, .provisional:
                    return PermissionStateDenied(shouldShowRationale: true)
                case .notDetermined:
                    return PermissionStateUnknown()
                @unknown default:
                    SLog.e("Unexpected code path")
                    return PermissionStateUnknown()
                }}()
                onNotificationPermissionResult(state)
            }
        }
    }
}
