import CoreHaptics

class Vibrator {
    private var hapticEngine: CHHapticEngine?
    private var player: CHHapticAdvancedPatternPlayer? = nil
    
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
            try hapticEngine?.start()
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
                    eventType: .hapticTransient, // Short vibration
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
            player = try hapticEngine.makeAdvancedPlayer(with: pattern)
            player?.loopEnabled = true
            try player?.start(atTime: CHHapticTimeImmediate)
        } catch {
            logger.warning("Failed to play haptic pattern: \(error)")
        }
    }
    
    func stopVibrations() {
        do {
            try player?.stop(atTime: CHHapticTimeImmediate)
        } catch {
            logger.warning("Error stopping haptics \(error)")
        }
    }
}
