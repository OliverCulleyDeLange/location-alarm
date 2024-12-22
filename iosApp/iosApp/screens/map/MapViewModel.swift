import Shared
import UserNotifications
import Foundation
import Combine
import KMPNativeCoroutinesCombine


/// App side view model. Extends the shared kotlin view model and adds IOS specific functions.
class MapViewModel: Shared.MapViewModel, Cancellable {
    
    private var notificationManager = NotificationManager.shared
    
    private var cancellables = Set<AnyCancellable>()
    
    override init(appStateStore: AppStateStore, uiStateMapper: MapAppStateToMapUiState) {
        super.init(appStateStore: appStateStore, uiStateMapper: uiStateMapper)
        
        listenAndRequestNotificationPermissions()
        
        checkNotificationPermissions()
    }
    
    func cancel() {
        for c in cancellables {
            c.cancel()
        }
        cancellables = []
    }
    
    fileprivate func listenAndRequestNotificationPermissions() {
        createPublisher(for: stateFlow)
            .assertNoFailure()
            .map { $0.shouldRequestNotificationPermissions }
            .removeDuplicates()
            .filter { $0 }
            .sink { shouldRequestNotificationPermissions in
                self.requestNotificationPermissions()
            }
            .store(in: &cancellables)
    }
    
    fileprivate func requestNotificationPermissions()  {
        notificationManager.requestPermissions{ granted in
            self.onEvent(uiEvent: UiResultNotificationPermissionResult(state: PermissionStateKt.permissionStateFrom(granted: granted)))
        }
    }
    
    fileprivate func checkNotificationPermissions()  {
        notificationManager.checkPermissions{ state in
            self.onEvent(uiEvent: UiResultNotificationPermissionResult(state: state))
        }
    }
    
}
