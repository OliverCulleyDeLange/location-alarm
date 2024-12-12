class AlarmManager {
    var vibrator: Vibrator = Vibrator()
    
    /// Begins playing alarm sounds and vibrations
    func startAlarm() {
        vibrator.startVibrating()
    }
    
    func stopAlarm() {
        vibrator.stopVibrations()
    }
}
