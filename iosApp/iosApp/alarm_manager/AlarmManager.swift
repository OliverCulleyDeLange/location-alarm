class AlarmManager {
    static let shared = AlarmManager()

    private var vibrator: SystemVibrator = SystemVibrator()
    private var alarmPlayer: AlarmPlayer = AlarmPlayer()
    
    /// Begins playing alarm sounds and vibrations
    func startAlarm() {
        vibrator.vibrate()
        alarmPlayer.playAlarm()
    }
    
    func stopAlarm() {
        vibrator.stop()
        alarmPlayer.stopAlarm()
    }
}
