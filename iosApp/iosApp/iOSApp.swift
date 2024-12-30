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
        KoinProvider.companion.doInitKoin()
        appStateStore = get()
        
#if DEBUG
        appStateStore.setDebug(debug: true)
#else
        appStateStore.setDebug(debug: false)
#endif
        alarmManager = AlarmManager(appStateStore: appStateStore)
        let locationService = LocationService()
        locationStateListener = LocationStateListener(locationService: locationService, appStateStore: appStateStore)
    }
    
    var body: some Scene {
        WindowGroup {
            AppUi(
                appStateStore: appStateStore, alarmManager: alarmManager
            ).onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                appStateStore.onAppForegrounded()
            }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.didEnterBackgroundNotification)) { _ in
                appStateStore.onAppBackgrounded()
            }
            .onOpenURL { url in
                if url.scheme == "uk.co.oliverdelange.locationalarm" {
                    SLog.i("Deeplink: \(url)")
                    switch url.relativePath {
                    case "/stop_alarm":
                        appStateStore.onSetAlarm(enabled: false)
                        
                    default:
                        SLog.w("Unhandled deeplink \(url.relativePath)")
                    }
                }
            }
        }
    }
}
