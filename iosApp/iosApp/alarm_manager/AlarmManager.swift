class AlarmManager {
    private var vibrator: Vibrator = Vibrator()
    private var alarmPlayer: AlarmPlayer = AlarmPlayer()
    
    /// Begins playing alarm sounds and vibrations
    func startAlarm() {
        vibrator.startVibrating()
        alarmPlayer.playAlarm()
    }
    
    func stopAlarm() {
        vibrator.stopVibrations()
        alarmPlayer.stopAlarm()
    }
}
