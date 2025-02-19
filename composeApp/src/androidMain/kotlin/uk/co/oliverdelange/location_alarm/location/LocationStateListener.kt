package uk.co.oliverdelange.location_alarm.location

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.store.AppStateStore

private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

class LocationStateListener(
    val appStateStore: AppStateStore,
    val locationService: LocationService
) {
    fun listenToStateAndListenForLocationUpdates() {
        SLog.d("LocationService init")
        serviceScope.launch {
            appStateStore.state
                .map { it.shouldListenForLocationUpdates }
                .distinctUntilChanged()
                .collect {
                    if (it) {
                        locationService.listenForUpdates()
                    } else {
                        locationService.stopListeningForUpdates()
                    }
                }
        }
    }
}