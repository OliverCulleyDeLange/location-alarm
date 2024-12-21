package uk.co.oliverdelange.locationalarm.model.ui

import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState

/** To allow passing a viewmodel into previews */
interface MapViewModelInterface {
    fun onTapAllowLocationPermissions()
    fun onRequestedLocationPermissions()
    fun onLocationPermissionResult(state: PermissionState)
    fun onNotificationPermissionResult(granted: Boolean)
    fun onNotificationPermissionResult(state: PermissionState)
    fun onLocationChange(locations: List<Location>)
    fun onRadiusChanged(radius: Int)
    fun onMapTap(newGeofenceLocation: Location)
    fun onMapShown()
    fun onMapNotShown()
    fun onTapLocationIcon()
    fun onFinishFlyingToUsersLocation()
    fun onTapStopAlarm()
    fun onToggleAlarm()
    fun onSetAlarm(enabled: Boolean)
    fun onToggleAlarmWithDelay()
}