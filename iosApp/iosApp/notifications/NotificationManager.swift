import UserNotifications


/// Handles firing local notifications
class NotificationManager {
    
    static let shared = NotificationManager()
    
    private let notificationId = "LocationAlarmNotification"
    
    func createAlarmNotification() {
        logger.debug("createAlarmNotification")
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
                logger.error("Error scheduling notification: \(error)")
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
                logger.debug("Notification permissions granted: \(granted)")
                await MainActor.run {
                    onNotificationPermissionResult(granted)
                }
            } catch {
                logger.debug("Error requesting notification permissions: \(error)")
            }
        }
    }
    
    func checkPermissions(onNotificationPermissionResult: @escaping (Bool) -> Void) {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            DispatchQueue.main.async {
                let hasNotificationPermission = settings.authorizationStatus == .authorized
                onNotificationPermissionResult(hasNotificationPermission)
            }
        }
    }
}
