import WatchConnectivity

class WatchSessionManager: NSObject, WCSessionDelegate, ObservableObject {
    static let shared = WatchSessionManager()
//    @Published var alarms: [Alarm] = []
    
    override init() {
        super.init()
        if WCSession.isSupported() {
            WCSession.default.delegate = self
            WCSession.default.activate()
        } else {
            print("Watch session not supported")

        }
    }
    
    func enableAlarm() {
        if WCSession.default.isReachable {
            WCSession.default.sendMessage(["request": "enableAlarm"], replyHandler: nil, errorHandler: nil)
        } else {
            print("Watch session not reachable")
        }
    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String: Any]) {
//        if let alarmData = message["alarms"] as? [[String: Any]] {
//            DispatchQueue.main.async {
//                self.alarms = alarmData.compactMap { dict in
//                    guard let id = dict["id"] as? String, let time = dict["time"] as? String else { return nil }
//                    return Alarm(id: id, time: time)
//                }
//            }
//        }
    }
    
    func session(_ session: WCSession, activationDidCompleteWith state: WCSessionActivationState, error: Error?) {}
}
