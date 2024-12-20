import SwiftUI
import KMPObservableViewModelSwiftUI
import Shared

@available(iOS 17.0, *)
@main
struct iOSApp: App {
    private var appStateStore: AppStateStore
    private var alarmManager: AlarmManager
    
    init() {
        appStateStore = AppStateStore(timeProvider: SystemTimeProvider())
        alarmManager = AlarmManager(appStateStore: appStateStore)
    }
    
    var body: some Scene {
        WindowGroup {
            AppUi(
                appStateStore: appStateStore,
                alarmManager: alarmManager
            )
        }
    }
}
