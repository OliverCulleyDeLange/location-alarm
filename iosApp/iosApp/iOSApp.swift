import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

@available(iOS 17.0, *)
@main
struct iOSApp: App {
    private var appStateStore: AppStateStore
    private var alarmManager: AlarmManager
    private var locationStateListener: LocationStateListener
    
    init() {
        appStateStore = AppStateStore(timeProvider: SystemTimeProvider())
        alarmManager = AlarmManager(appStateStore: appStateStore)
        let locationService = LocationService()
        locationStateListener = LocationStateListener(locationService: locationService, appStateStore: appStateStore)
    }
    
    var body: some Scene {
        WindowGroup {
            AppUi(
                appStateStore: appStateStore,
                alarmManager: alarmManager
            ).onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                appStateStore.onAppForegrounded()
            }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.didEnterBackgroundNotification)) { _ in
                appStateStore.onAppBackgrounded()
            }
        }
    }
}
