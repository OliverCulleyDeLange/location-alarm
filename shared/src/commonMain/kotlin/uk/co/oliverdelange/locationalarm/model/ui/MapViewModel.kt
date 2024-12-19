package uk.co.oliverdelange.locationalarm.model.ui

import co.touchlab.kermit.Logger
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToMapUiState
import uk.co.oliverdelange.locationalarm.model.domain.AppStateStore
import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState

open class MapViewModel(
    private val appStateStore: AppStateStore,
    private val uiStateMapper: MapAppStateToMapUiState,
) : ViewModel() {

    @NativeCoroutinesState
    val state: StateFlow<MapUiState> = appStateStore.state
        .map(uiStateMapper::map)
        .stateIn(viewModelScope, SharingStarted.Lazily, MapUiState())

    init {
        viewModelScope.launch {
            state.map { it.toDebugString() }.distinctUntilChanged().collect {
                // This only logs when the debug string changes, so if you wanna track something, add it.
                Logger.w("NEW MAP UI STATE: $it")
            }
        }
    }

    fun onTapAllowLocationPermissions() {
        appStateStore.onTapAllowLocationPermissions()
//        Logger.d { "onTapAllowLocationPermissions" }
//        _state.update { it.copy(shouldRequestLocationPermissions = true) }
    }

    fun onRequestedLocationPermissions() {
        appStateStore.onRequestedLocationPermissions()
//        _state.update { it.copy(shouldRequestLocationPermissions = false) }
    }

    fun onLocationPermissionResult(state: PermissionState) {
        appStateStore.onLocationPermissionResult(state)
//        Logger.d { "Location permission updated: $state" }
//        _state.update { current ->
//            current.copy(
//                locationPermissionState = state,
//                shouldRequestLocationPermissions = false,
//            )
//        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        appStateStore.onNotificationPermissionResult(granted)
//        onNotificationPermissionResult(if (granted) PermissionState.Granted else PermissionState.Denied(false))
    }

    fun onNotificationPermissionResult(state: PermissionState) {
        appStateStore.onNotificationPermissionResult(state)
//        Logger.d { "Notification permission updated: $state" }
//        appStateStore.onNotificationPermissionResult(state)
//        _state.update { current ->
//            current.copy(
//                notificationPermissionState = state,
//                shouldRequestNotificationPermissions = false,
//            )
//        }
//
//        // If notification permissions are granted and the user is requesting the enable the alarm, we should honor this
//        if (state == PermissionState.Granted && _state.value.userRequestedAlarmEnable) {
//            onSetAlarm(true)
//        }
    }

    /** If users location changes, and user hasn't interacted with the map yet, make the geofence follow the location */
    fun onLocationChange(locations: List<Location>) {
        appStateStore.onLocationChange(locations)
//        Logger.v { "Location updated $locations" }
//        locations.firstOrNull()?.let { firstLocation ->
//            _state.update {
//                it.copy(
//                    usersLocation = firstLocation,
//                    usersLocationHistory = it.usersLocationHistory.plus(firstLocation)
//                )
//            }
//            if (!_state.value.mapInteracted) {
//                _state.update {
//                    it.copy(geoFenceLocation = firstLocation)
//                }
//            }
//            recomputeDistancesAndTriggered()
//        } ?: Logger.w { "Location update contains no location" }
    }

    fun onRadiusChanged(radius: Int) {
        appStateStore.onRadiusChanged(radius)
//        val newRadius = radius.coerceAtLeast(10)
//        _state.update { state ->
//            state.copy(
//                perimeterRadiusMeters = newRadius,
//                distanceToGeofencePerimeter = getDistanceToGeofencePerimeter(state.usersLocation, state.geoFenceLocation, newRadius)
//            )
//        }
    }

    fun onMapTap(newGeofenceLocation: Location) {
        appStateStore.onMapTap(newGeofenceLocation)
//        Logger.d("Map tapped $newGeofenceLocation")
//        _state.update { state ->
//            state.copy(
//                mapInteracted = true,
//                geoFenceLocation = newGeofenceLocation,
//                distanceToGeofence = getDistanceToGeofence(state.usersLocation, newGeofenceLocation),
//                distanceToGeofencePerimeter = getDistanceToGeofencePerimeter(state.usersLocation, newGeofenceLocation, state.perimeterRadiusMeters)
//            )
//        }
    }

    fun onTapLocationIcon() {
        appStateStore.onTapLocationIcon()
//        _state.update { state ->
//            state.copy(usersLocationToFlyTo = state.usersLocation)
//        }
    }

    fun onFinishFlyingToUsersLocation() {
        appStateStore.onFinishFlyingToUsersLocation()
//        _state.update { state ->
//            state.copy(usersLocationToFlyTo = null)
//        }
    }

    fun onTapStopAlarm() {
        appStateStore.onTapStopAlarm()
//        onSetAlarm(false)
    }

    /** Check notification permissions and enable if granted
     * Otherwise, request notification permissions
     * @param delay: Dev tool do delay alarm triggering by 5 seconds */
    fun onToggleAlarm() {
        appStateStore.onToggleAlarm()
//        when (_state.value.notificationPermissionState) {
//            PermissionState.Granted -> {
//                val alarmEnabled = !state.value.alarmEnabled
//                onSetAlarm(alarmEnabled)
//            }
//
//            else -> {
//                _state.update { state ->
//                    state.copy(
//                        userRequestedAlarmEnable = true,
//                        shouldRequestNotificationPermissions = true,
//                    )
//                }
//            }
//        }
    }

    fun onSetAlarm(enabled: Boolean) {
        appStateStore.onSetAlarm(enabled)
//        _state.update { state ->
//            state.copy(
//                alarmEnabled = enabled,
//                alarmEnabledAt = if (enabled) timeProvider.now() else state.alarmEnabledAt,
//                mapInteracted = true,
//                userRequestedAlarmEnable = if (enabled) false else state.userRequestedAlarmEnable,
//            )
//        }
//        recomputeDistancesAndTriggered()
//        if (!enabled) delayAlarmTriggering = false
    }

    // Dev tool to allow enabling the alarm, but not allow triggering until a given time has passed
    fun onToggleAlarmWithDelay() {
        appStateStore.onToggleAlarmWithDelay()
//        delayAlarmTriggering = true
//        onToggleAlarm()
//        viewModelScope.launch {
//            delay(5000)
//            recomputeDistancesAndTriggered()
//        }
    }

//    private fun getDistanceToGeofence(
//        usersLocation: Location?,
//        geofenceLocation: Location?
//    ): Int? {
//        return if (usersLocation != null && geofenceLocation != null) {
//            geofenceLocation.distanceTo(usersLocation).roundToInt()
//        } else null
//    }
//
//    private fun getDistanceToGeofencePerimeter(
//        usersLocation: Location?,
//        geofenceLocation: Location?,
//        perimeterRadiusMeters: Int
//    ): Int? {
//        return getDistanceToGeofence(usersLocation, geofenceLocation)?.minus(perimeterRadiusMeters)
//    }
}
