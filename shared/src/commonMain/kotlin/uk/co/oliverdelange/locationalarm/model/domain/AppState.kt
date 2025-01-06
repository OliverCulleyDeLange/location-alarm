package uk.co.oliverdelange.locationalarm.model.domain

import kotlinx.datetime.Instant
import uk.co.oliverdelange.locationalarm.logging.LoggedProperties
import uk.co.oliverdelange.locationalarm.logging.appStateTrackedProperties
import uk.co.oliverdelange.locationalarm.navigation.Navigate
import uk.co.oliverdelange.locationalarm.navigation.Route

data class AppState(
    val debug: Boolean = false,
    val appInForeground: Boolean = true,
    val currentScreen: Route = Route.LocationPermissionRequiredScreen,
    val navigateTo: Navigate? = null,

    val shouldRequestNotificationPermissions: Boolean = false,
    val notificationPermissionState: PermissionState = PermissionState.Unchecked,
    /** Android only restarts the app when permission become denied, so when a user grants a permission via settings
     * we don't hear about it. So we should check all permission state when the app is foregrounded */
    val shouldCheckNotificationPermissions: Boolean = false,

    val shouldRequestLocationPermissions: Boolean = false,
    val locationPermissionState: PermissionState = PermissionState.Unchecked,
    /** Android only restarts the app when permission become denied, so when a user grants a permission via settings
     * we don't hear about it. So we should check all permission state when the app is foregrounded */
    val shouldCheckLocationPermissions: Boolean = false,


    // Listen if map is open or alarm is enabled
    val shouldListenForLocationUpdates: Boolean = false,

    // TODO The below are really MapFeatureState
    // Most up to date user location - may be used for the geofence if they haven't manually moved it
    val usersLocation: Location? = null,
    // A store of all location updates while the app is running
    val usersLocationHistory: List<LocationUpdate> = emptyList(),
    // The location of the geofence. Defaults to the usersLocation, unless manually moved.
    val geoFenceLocation: Location? = null,
    // Flag to say whether user has manually interacted with the map.
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
) : LoggedProperties<AppState> {
    override fun getTrackedProperties() = appStateTrackedProperties
}