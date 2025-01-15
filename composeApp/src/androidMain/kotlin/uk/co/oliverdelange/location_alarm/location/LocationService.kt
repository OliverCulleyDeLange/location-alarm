package uk.co.oliverdelange.location_alarm.location

interface LocationService {
    fun listenToStateAndListenForLocationUpdates()
    fun onLocationUpdate(location: uk.co.oliverdelange.locationalarm.model.domain.Location)
}