import AudioToolbox

/// Uses AudioServices to play system vibrations. Apparently this should work from the background. 
class SystemVibrator {
    // Whether the vibrations should play because vibrate has been called
    private var shouldVibrate: Bool = false
    // Whether the vibration is currently playing - used for looping mechanism
    private var isVibrating: Bool = false
    // Task which runs the vibration, for cancallation
    private var task: Task<Void, any Error>? = nil
    
    func vibrate() {
        shouldVibrate = true
        task = Task {
            while (shouldVibrate){
                if (isVibrating){
                    try await sleepFor(milliseconds: 100)
                } else {
                    isVibrating = true
                    AudioServicesPlaySystemSoundWithCompletion(kSystemSoundID_Vibrate) {
                        self.isVibrating = false
                    }
                }
            }
        }
    }
    
    func stop() {
        shouldVibrate = false
        task?.cancel()
    }
}
