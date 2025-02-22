package uk.co.oliverdelange.locationalarm.model.ui.map

import uk.co.oliverdelange.locationalarm.logging.LoggedProperties
import uk.co.oliverdelange.locationalarm.logging.mapUiStateTrackedProperties
import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.model.ui.UiState

data class MapUiState(
    // Alert dialog which appears when alarm is triggered - gives option to stop alarm
    val shouldShowAlarmAlert: Boolean = false,
    val toggleAlarmButtonText: String = "",
    /** Enable the alarm button if:
     * - User has not denied notification permissions
     * - We know the users current location
     * - Geofence is set
     * */
    val enableAlarmButtonEnabled: Boolean = false,
    val shouldRequestNotificationPermissions: Boolean = false,
    // If a user denied notification permissions when trying to enable the alarm, show a UI message
    val shouldShowNotificationPermissionDeniedMessage: Boolean = false,
    val shouldShowNotificationPermissionRationale: Boolean = false,
    val shouldRequestLocationPermissions: Boolean = false,
    val shouldEnableMapboxLocationComponent: Boolean = false,
    val shouldShowDebugTools: Boolean = false,
    // Most up to date user location
    val usersLocation: Location? = null,
    // The location of the geofence. Defaults to the usersLocation, unless manually moved.
    val geoFenceLocation: Location? = null,
    // When this is set, the UI should fly to the given location
    val usersLocationToFlyTo: Location? = null,
    // Geofence radius in meters
    val perimeterRadiusMeters: Int = 200,
    val shouldShowDistanceToAlarmText: Boolean = false,
    // Distance to alarm shown to users
    val distanceToAlarmText: String = "",
) : UiState, LoggedProperties<MapUiState> {
    override fun getTrackedProperties() = mapUiStateTrackedProperties
}

