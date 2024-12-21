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


class EmptyMapViewModel : MapViewModelInterface {
    override fun onTapAllowLocationPermissions() {

    }

    override fun onRequestedLocationPermissions() {
    }

    override fun onLocationPermissionResult(state: PermissionState) {
    }

    override fun onNotificationPermissionResult(granted: Boolean) {
    }

    override fun onNotificationPermissionResult(state: PermissionState) {
    }

    override fun onLocationChange(locations: List<Location>) {
    }

    override fun onRadiusChanged(radius: Int) {
    }

    override fun onMapTap(newGeofenceLocation: Location) {
    }

    override fun onMapShown() {
    }

    override fun onMapNotShown() {
    }

    override fun onTapLocationIcon() {
    }

    override fun onFinishFlyingToUsersLocation() {
    }

    override fun onTapStopAlarm() {
    }

    override fun onToggleAlarm() {
    }

    override fun onSetAlarm(enabled: Boolean) {
    }

    override fun onToggleAlarmWithDelay() {
    }

}