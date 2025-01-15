package uk.co.oliverdelange.location_alarm.location

import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.store.AppStateStore

class FakeLocationService(val appStateStore: AppStateStore) : LocationService {
    override fun onLocationUpdate(location: Location) {
        appStateStore.onLocationChange(locations = listOf(location))
    }

    override fun listenToStateAndListenForLocationUpdates() {
        // no op
    }
}