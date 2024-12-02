package model.domain

import co.touchlab.kermit.Logger

data class AppState(
    val locationPermissionState: PermissionState = PermissionState.Unknown,
    // Most up to date user location - may be used fort the geofence if they haven't manually moved it
    val usersLocation: Location? = null,
    // The location of the geofence. Defaults to the usersLocation, unless manually moved.
    val geoFenceLocation: Location? = null,
    // Flag to say whether user has manually interacted with the mao.
    val mapInteracted: Boolean = false,
    // Geofence radius in meters
    val perimeterRadiusMeters: Int = 500,
    // Whether the user has enabled the alarm
    val alarmEnabled: Boolean = true, // FIXME testing only
    // Whether the alarm has been triggered (the users location is within the geofence bounds)
    val alarmTriggered: Boolean = false,
) {
    fun shouldTriggerAlarm(): Boolean {
        if (!alarmEnabled) return false

        return if (geoFenceLocation != null && usersLocation != null) {
            val distance = geoFenceLocation.distanceTo(usersLocation)
            Logger.d("Distance $distance, radius $perimeterRadiusMeters")
            distance < perimeterRadiusMeters
        } else false
    }
}