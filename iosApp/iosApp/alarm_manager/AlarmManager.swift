import AVFAudio
import Combine
import KMPNativeCoroutinesCombine
import Shared

/// Listens to alarm triggered state and orchestrates the various parts of the alarm like sound, vibration, notifications etc
class AlarmManager {
    private var appStateStore: AppStateStore
    
    private var cancellables = Set<AnyCancellable>()

    init(appStateStore: AppStateStore) {
        self.appStateStore = appStateStore
        self.setAudioSessionCategory()
        self.whenAlarmTriggeredSoundAlarmAndVibrate()
        self.whenAlarmEnabledToggleAudioSession()
        /// Listen to location updates and start live activity when alarm enabled
        if #available(iOS 16.2, *) {
            self.handleLiveActivityLifecycle()
            self.updateLiveActivityWhileAlarmActive()
        }
    }

    private var vibrator: SystemVibrator = SystemVibrator()
    private var alarmPlayer: AlarmPlayer = AlarmPlayer()
    private let audioSession = AVAudioSession.sharedInstance()
    private var notificationManager = NotificationManager.shared

    
    fileprivate func setAudioSessionCategory() {
        do {
            try audioSession.setCategory(.playback, mode: .default, policy: .default, options: .mixWithOthers)
            logger.debug("Set audio session category")
        } catch {
            logger.warning("Failed to set the audio session configuration")
        }
    }
    
    fileprivate func whenAlarmTriggeredSoundAlarmAndVibrate() {
        createPublisher(for: appStateStore.stateFlow)
            .assertNoFailure()
            .map { $0.alarmTriggered }
            .removeDuplicates()
            .sink { alarmTriggered in
                if (alarmTriggered){
                    self.startAlarm()
                    self.notificationManager.createAlarmNotification()
                } else {
                    self.stopAlarm()
                    self.notificationManager.removeAlarmNotification()
                }
            }
            .store(in: &cancellables)
    }
    
    fileprivate func whenAlarmEnabledToggleAudioSession() {
        createPublisher(for: appStateStore.stateFlow)
            .assertNoFailure()
            .map { $0.alarmEnabled }
            .removeDuplicates()
            .sink { enabled in
                if (enabled){
                    self.activateAudioSession()
                } else {
                    self.deactivateAudioSession()
                }
            }
            .store(in: &cancellables)
    }
    
    @available(iOS 16.2, *)
    fileprivate func handleLiveActivityLifecycle() {
        createPublisher(for: appStateStore.stateFlow)
            .assertNoFailure()
            .map { state in state.alarmEnabled }
            .removeDuplicates()
            .sink { alarmEnabled in
                Task {
                    if (alarmEnabled){
                        guard let distanceToAlarm = self.appStateStore.state.distanceToGeofencePerimeter else {
                            logger.warning("Trying to create live activity, but no available distance to alarm")
                            return
                        }
                        await LiveActivityManager.shared.createLiveActivity(
                            distanceToAlarm.intValue,
                            self.appStateStore.state.alarmTriggered
                        )
                    } else {
                        await LiveActivityManager.shared.stopLiveActivity()
                    }
                }
            }
            .store(in: &cancellables)
    }
    
    @available(iOS 16.2, *)
    fileprivate func updateLiveActivityWhileAlarmActive() {
        createPublisher(for: appStateStore.stateFlow)
            .assertNoFailure()
            .filter { state in state.alarmEnabled}
            .map { DistanceAndTriggered(
                distanceToGeofencePerimeter: $0.distanceToGeofencePerimeter?.intValue,
                alarmTriggered: $0.alarmTriggered
            ) }
            .removeDuplicates()
            .sink { holder in
                guard let distanceToAlarm = holder.distanceToGeofencePerimeter else {
                    logger.warning("Trying to update live location, but no available distance to alarm")
                    return
                }
                Task {
                    await LiveActivityManager.shared.updatePersistentNotification(
                        distanceToAlarm,
                        holder.alarmTriggered
                    )
                }
            }
            .store(in: &cancellables)
    }
    
    private func activateAudioSession() {
        do {
            try audioSession.setActive(true)
            logger.debug("Audio session activated")
        }
        catch {
            logger.warning("Error activating audio session: \(error)")
        }
    }

    private func deactivateAudioSession() {
        do {
            try audioSession.setActive(false)
            logger.debug("Audio session deactivated")
        }
        catch {
            logger.warning("Error deactivating audio session: \(error)")
        }
    }
    
    /// Begins playing alarm sounds and vibrations
    private func startAlarm() {
        logger.warning("STARTING ALARM")
        vibrator.vibrate()
        alarmPlayer.playAlarm()
    }
    
    private func stopAlarm() {
        logger.warning("STOPPING ALARM")
        vibrator.stop()
        alarmPlayer.stopAlarm()
    }
}

// TODO This is annoying to have to have, but i couldn't work out how to use a tuple with the publisher chain
private struct DistanceAndTriggered: Equatable {
    let distanceToGeofencePerimeter: Int?
    let alarmTriggered: Bool
}
