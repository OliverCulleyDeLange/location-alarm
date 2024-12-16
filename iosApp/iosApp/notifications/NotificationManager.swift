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
}
