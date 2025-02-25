import WatchConnectivity
import Shared

class WatchConnectivityManager: NSObject, WCSessionDelegate {
    private var appStateStore: AppStateStore
    init(appStateStore: AppStateStore) {
        self.appStateStore = appStateStore
        super.init()
        if WCSession.isSupported() {
            WCSession.default.delegate = self
            WCSession.default.activate()
        } else {
            SLog.w("Watch connectivity not supported")
        }
    }
    
//    func sendAlarmsToWatch(_ alarms: [Alarm]) {
//        guard WCSession.default.isReachable else { return }
//        let alarmData = alarms.map { ["id": $0.id, "time": $0.time] } // Convert to dictionary
//        WCSession.default.sendMessage(["alarms": alarmData], replyHandler: nil, errorHandler: nil)
//    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String : Any]) {
        if let request = message["request"] as? String, request == "enableAlarm" {
//            let alarms = AlarmManager.shared.getAlarms()
//            sendAlarmsToWatch(alarms)
            SLog.w("Enabling alarm via watch app")
            appStateStore.onSetAlarm(enabled: true)
        }
    }
    
    func session(_ session: WCSession, activationDidCompleteWith state: WCSessionActivationState, error: Error?) {}
    func sessionDidBecomeInactive(_ session: WCSession) {}
    func sessionDidDeactivate(_ session: WCSession) { WCSession.default.activate() }
}
