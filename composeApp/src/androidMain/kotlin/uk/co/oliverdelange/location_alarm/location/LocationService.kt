package uk.co.oliverdelange.location_alarm.location

interface LocationService {
    fun onLocationUpdate(location: uk.co.oliverdelange.locationalarm.model.domain.Location)
}