
import AudioToolbox

/// Handles playing alarm sounds 
class AlarmPlayer {
    // Whether the alarm sound shoul play because playAlarm has been called
    private var shouldPlay: Bool = false
    // Whether the alarm sound is currently playing - used for sound looping mechanism
    private var isPlaying: Bool = false
    // Task which runs the sound playing, for cancallation
    private var task: Task<Void, any Error>? = nil
    
    func playAlarm() {
        shouldPlay = true
        task = Task {
            while (shouldPlay){
                if (isPlaying){
                    try await sleepFor(milliseconds: 100)
                } else {
                    isPlaying = true
                    // https://github.com/TUNER88/iOSSystemSoundsLibrary
                    AudioServicesPlaySystemSoundWithCompletion(1304) {
                        self.isPlaying = false
                    }
                }
            }
        }
    }
    
    func stopAlarm() {
        shouldPlay = false
        task?.cancel()
    }
}
