package model.domain

import kotlinx.datetime.Instant

data class MapFeatureState(
    val shouldRequestNotificationPermissions: Boolean = false,
    val notificationPermissionState: PermissionState = PermissionState.Unknown,

    val shouldRequestLocationPermissions: Boolean = false,
    val locationPermissionState: PermissionState = PermissionState.Unknown,

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


    /* Dev tools */
    /** Whether to delay alarm triggering by a harccoded 5 seconds. This is a dumb hack to manually test in a situation where you don't have reliable GPS (like indoors at your desk)
     * TODO this shouldn't really be in prod code - how to extract it out into debug only code
     * */
    val delayAlarmTriggering: Boolean = false,
) : AppState {
    fun toDebugString() =
        "Enabled: ${alarmEnabled}, Triggered: ${alarmTriggered}, delayAlarmTriggering: ${delayAlarmTriggering}, distanceToGeofencePerimeter: $distanceToGeofencePerimeter"

    /** Enable the alarm button if:
     * - User has not denied notification permissions
     * - We know the users current location
     * - Geofence is set
     * */
    val enableAlarmButtonEnabled = notificationPermissionState !is PermissionState.Denied &&
        usersLocation != null &&
        geoFenceLocation != null

    // If a user denied notification permissions when trying to enable the alarm, show a UI message
    val shouldShowNotificationPermissionDeniedMessage = notificationPermissionState is PermissionState.Denied

}