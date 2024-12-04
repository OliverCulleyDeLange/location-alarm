package model.domain

import co.touchlab.kermit.Logger

data class AppState(
    val notificationPermissionState: PermissionState = PermissionState.Unknown,
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
    val alarmEnabled: Boolean = false,
    // Whether the alarm has been triggered (the users location is within the geofence bounds)
    val alarmTriggered: Boolean = false,
) {
    fun shouldTriggerAlarm() = shouldTriggerAlarm(alarmEnabled, geoFenceLocation, usersLocation, perimeterRadiusMeters)
}

/** Determines whether the alarm should trigger based on the passed in values */
fun shouldTriggerAlarm(alarmEnabled: Boolean, geoFenceLocation: Location?, usersLocation: Location?, perimeterRadiusMeters: Int): Boolean {
    if (!alarmEnabled) return false

    return if (geoFenceLocation != null && usersLocation != null) {
        val distance = geoFenceLocation.distanceTo(usersLocation)
        Logger.v("Distance $distance, radius $perimeterRadiusMeters")
        distance < perimeterRadiusMeters
    } else false
}