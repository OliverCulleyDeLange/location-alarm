package model.domain

data class MapFeatureState(
    val notificationPermissionState: PermissionState = PermissionState.Unknown,
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
    // The distance in meters from the users location to the geofence perimeter (distance until alarm sounds)
    val distanceToGeofencePerimeter: Int? = null,
    // The distance in meters from the users location to the geofence location
    val distanceToGeofence: Int? = null,
    // Tapping on the location icon zooms to the users current location.
    // If this is set we should fly to it. The UI should listen for changes to this state and fly to new values.
    val usersLocationToFlyTo: Location? = null,


    /* Dev tools */
    /** The number of locations that need to be in [usersLocationHistory] to allow triggering the alarm
     * This is to allow delaying the alarm for testing on real devices
     * TODO this shouldn't really be in prod code - how to extract it out into debug only code
     * */
    val alarmTriggerDelayLocationHistorySize: Int = 0,
) : AppState {
    /** Whether the alarm should be allowed to trigger due to [alarmTriggerDelayLocationHistorySize] */
    private val alarmNotDelayed = usersLocationHistory.size > alarmTriggerDelayLocationHistorySize

    /** Enable the alarm button if:
     * - User has not denied notification permissions
     * - We know the users current location
     * - Geofence is set
     * */
    val enableAlarmButtonEnabled = notificationPermissionState !is PermissionState.Denied &&
        usersLocation != null &&
        geoFenceLocation != null

    // Whether the alarm has been triggered (the users location is within the geofence bounds)
    val alarmTriggered = alarmNotDelayed && alarmEnabled && distanceToGeofencePerimeter?.let { it <= 0 } ?: false

    // If a user denied notification permissions when trying to enable the alarm, show a UI message
    val shouldShowNotificationPermissionDeniedMessage = notificationPermissionState is PermissionState.Denied

}