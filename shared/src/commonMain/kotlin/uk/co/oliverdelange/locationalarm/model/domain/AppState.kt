package uk.co.oliverdelange.locationalarm.model.domain

import kotlinx.datetime.Instant

data class AppState(
    val shouldRequestNotificationPermissions: Boolean = false,
    val notificationPermissionState: PermissionState = PermissionState.Unknown,

    val shouldRequestLocationPermissions: Boolean = false,
    val locationPermissionState: PermissionState = PermissionState.Unknown,

    // Listen if map is open or alarm is enabled
    val shouldListenForLocationUpdates: Boolean = false,

    // Most up to date user location - may be used fort the geofence if they haven't manually moved it
    val usersLocation: Location? = null,
    // A store of all location updates while the app is running
    val usersLocationHistory: List<Location> = emptyList(),
    // The location of the geofence. Defaults to the usersLocation, unless manually moved.
    val geoFenceLocation: Location? = null,
    // Flag to say whether user has manually interacted with the mao.
    val mapInteracted: Boolean = false,
    // Geofence radius in meters
    val perimeterRadiusMeters: Int = 200,
    /** Indicates the user has requested to enable the alarm but they haven't allowed notification permissions
     * We request permissions based on this flag, and on granted permission result, enable the alarm */
    val userRequestedAlarmEnable: Boolean = false,
    // Whether the user has enabled the alarm
    val alarmEnabled: Boolean = false,
    // The instant that the alarm was enabled at
    val alarmEnabledAt: Instant? = null,
    // Whether the alarm has been triggered (the users location is within the geofence bounds)
    val alarmTriggered: Boolean = false,
    // The distance in meters from the users location to the geofence perimeter (distance until alarm sounds)
    val distanceToGeofencePerimeter: Int? = null,
    // The distance in meters from the users location to the geofence location
    val distanceToGeofence: Int? = null,
    // Tapping on the location icon zooms to the users current location.
    // If this is set we should fly to it. The UI should listen for changes to this state and fly to new values.
    val usersLocationToFlyTo: Location? = null,
) {
    fun toDebugString() = """AppState:  ⤵︎
        |   notificationPermissionState: $notificationPermissionState
        |   locationPermissionState: $locationPermissionState
        |   alarmEnabled: ${alarmEnabled}
        |   alarmTriggered: ${alarmTriggered}
        |   delayAlarmTriggering: $delayAlarmTriggering
        |   distanceToGeofencePerimeter: $distanceToGeofencePerimeter
        |
    """.trimMargin()
}