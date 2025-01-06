import Shared
import Combine
import KMPNativeCoroutinesCombine

/// Listens to app state and starts/stops listening for location updates appropriately
class NotificationStateListener {
    private let notificationManager: NotificationManager
    private let appStateStore: Shared.AppStateStore
    
    // TODO Do we need to cancel these? This should live for the lifecycle of the app...
    private var cancellables = Set<AnyCancellable>()
    
    init(notificationService: NotificationManager, appStateStore: Shared.AppStateStore) {
        self.notificationManager = notificationService
        self.appStateStore = appStateStore
        
        listenAndRequestNotificationPermissions()
        listenAndCheckNotificationPermissions()
    }
    
    fileprivate func listenAndRequestNotificationPermissions() {
        createPublisher(for: appStateStore.stateFlow)
            .assertNoFailure()
            .map { $0.shouldRequestNotificationPermissions }
            .removeDuplicates()
            .filter { $0 }
            .sink { shouldRequestNotificationPermissions in
                self.requestNotificationPermissions()
            }
            .store(in: &cancellables)
    }
    
    fileprivate func listenAndCheckNotificationPermissions() {
        createPublisher(for: appStateStore.stateFlow)
            .assertNoFailure()
            .map { $0.shouldCheckNotificationPermissions }
            .removeDuplicates()
            .filter { $0 }
            .sink { shouldCheckNotificationPermissions in
                self.checkNotificationPermissions()
            }
            .store(in: &cancellables)
    }
    
    fileprivate func requestNotificationPermissions()  {
        notificationManager.requestPermissions { granted in
            self.appStateStore.onNotificationPermissionResult(granted: granted)
        }
    }
    
    fileprivate func checkNotificationPermissions()  {
        notificationManager.checkPermissions { state in
            self.appStateStore.onNotificationPermissionResult(state: state)
            self.appStateStore.onNotificationPermissionChecked()
        }
    }
}
