import AVFAudio
class AlarmManager {
    static let shared = AlarmManager()

//    private var vibrator: Vibrator = Vibrator()
    private var vibrator: SystemVibrator = SystemVibrator()
    private var alarmPlayer: AlarmPlayer = AlarmPlayer()
    private let audioSession = AVAudioSession.sharedInstance()
    
    init() {
       do {
           try audioSession.setCategory(.playback, mode: .default)
           logger.debug("Set audio session category")
       } catch {
           logger.warning("Failed to set the audio session configuration")
       }
    }
    
    func activateAudioSession() {
        do {
            try audioSession.setActive(true)
            logger.debug("Audio session activated")
        }
        catch {
            logger.warning("Error activating audio session: \(error)")
        }
    }

    func deactivateAudioSession() {
        do {
            try audioSession.setActive(false)
            logger.debug("Audio session deactivated")
        }
        catch {
            logger.warning("Error deactivating audio session: \(error)")
        }
    }
    
    /// Begins playing alarm sounds and vibrations
    func startAlarm() {
        logger.warning("STARTING ALARM")
        vibrator.vibrate()
        alarmPlayer.playAlarm()
    }
    
    func stopAlarm() {
        logger.warning("STOPPING ALARM")
        vibrator.stop()
        alarmPlayer.stopAlarm()
    }
}
