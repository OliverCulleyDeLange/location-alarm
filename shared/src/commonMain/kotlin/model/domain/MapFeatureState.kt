package model.domain

data class MapFeatureState(
    val notificationPermissionState: PermissionState = PermissionState.Unknown,
    val locationPermissionState: PermissionState = PermissionState.Unknown,
    // Most up to date user location - may be used fort the geofence if they haven't manually moved it
    val usersLocation: Location? = null,
    // The location of the geofence. Defaults to the usersLocation, unless manually moved.
    val geoFenceLocation: Location? = null,
    // Flag to say whether user has manually interacted with the mao.
    val mapInteracted: Boolean = false,
    // Geofence radius in meters
    val perimeterRadiusMeters: Int = 200,
    // Indicates the user has requested to enable the alarm. This may or may not complete due to notification permissions state.
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
) : AppState {
    // Whether the alarm has been triggered (the users location is within the geofence bounds)
    val alarmTriggered = alarmEnabled && distanceToGeofencePerimeter?.let { it <= 0 } ?: false
}