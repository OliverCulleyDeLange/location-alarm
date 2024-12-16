import CoreHaptics

/// Sexy class handling good vibrations. Sadly this doesn't work from the background so is unused. 
class Vibrator {
    private var hapticEngine: CHHapticEngine?
    private var vibrationPattern: CHHapticAdvancedPatternPlayer? = nil
    private var isEngineRunning: Bool = false
    
    init() {
        prepareHapticEngine()
    }
    
    private func prepareHapticEngine() {
        guard CHHapticEngine.capabilitiesForHardware().supportsHaptics else {
            logger.warning("Device does not support haptics")
            return
        }
        
        do {
            hapticEngine = try CHHapticEngine()
            
            // Handle engine reset
            hapticEngine?.resetHandler = { [weak self] in
                logger.debug("Haptic engine was reset.")
                self?.isEngineRunning = false
                do {
                    try self?.hapticEngine?.start()
                    self?.isEngineRunning = true
                } catch {
                    logger.debug("Failed to restart haptic engine: \(error.localizedDescription)")
                }
            }
            
            // Handle engine stop
            hapticEngine?.stoppedHandler = { [weak self] reason in
                logger.debug("Haptic engine stopped for reason: \(reason.rawValue)")
                self?.isEngineRunning = false
            }
        } catch {
            logger.warning("Failed to start haptic engine: \(error)")
        }
    }
    
    func startVibrating() {
        guard let hapticEngine = hapticEngine else {
            logger.warning("Tried to use haptics with uninitialised haptic engine")
            return
        }
        
        do {
            let events: [CHHapticEvent] = [
                CHHapticEvent(
                    eventType: .hapticTransient,
                    parameters: [],
                    relativeTime: 0.0
                ),
                CHHapticEvent(
                    eventType: .hapticTransient,
                    parameters: [],
                    relativeTime: 0.2
                ),
                CHHapticEvent(
                    eventType: .hapticContinuous,
                    parameters: [],
                    relativeTime: 0.5,
                    duration: 1.0
                )
            ]
            
            let pattern = try CHHapticPattern(events: events, parameters: [])
            vibrationPattern = try hapticEngine.makeAdvancedPlayer(with: pattern)
            vibrationPattern?.loopEnabled = true
            
            if !isEngineRunning {
                try hapticEngine.start()
                isEngineRunning = true
            }
            
            try vibrationPattern?.start(atTime: CHHapticTimeImmediate)
        } catch {
            logger.warning("Failed to play haptic pattern: \(error)")
        }
    }
    
    func stopVibrations() {
        if isEngineRunning {
            do {
                try vibrationPattern?.stop(atTime: CHHapticTimeImmediate)
            } catch {
                logger.warning("Error stopping haptics \(error)")
            }
        }
    }
}
