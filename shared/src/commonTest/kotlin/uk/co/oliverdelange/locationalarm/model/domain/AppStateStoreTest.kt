import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.model.domain.LocationUpdate
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState
import uk.co.oliverdelange.locationalarm.provider.MockTimeProvider
import uk.co.oliverdelange.locationalarm.store.AppStateStore
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AppStateStoreTest {

    private lateinit var timeProvider: MockTimeProvider

    private lateinit var store: AppStateStore
    private val someLocation = Location(51.49434994606447, 0.04302039031141709) // The reach
    private val someOtherLocation = Location(51.49332877322526, -0.0607650466109374) // The arch
    private val notGrantedPermissions = listOf(PermissionState.Unknown, PermissionState.Denied(true), PermissionState.Denied(false))

    @BeforeTest
    fun before() {
        Dispatchers.setMain(StandardTestDispatcher())
        timeProvider = MockTimeProvider()
        store = AppStateStore(timeProvider)
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLocationPermissionFLow() {
        assertEquals(false, store.state.value.shouldRequestLocationPermissions)
        // User taps allow button on our landing screen
        store.onTapAllowLocationPermissions()
        assertEquals(true, store.state.value.shouldRequestLocationPermissions)
        // We have requested location permissions from the system
        store.onRequestedLocationPermissions()
        assertEquals(false, store.state.value.shouldRequestLocationPermissions)
        assertEquals(PermissionState.Unchecked, store.state.value.locationPermissionState)
        // User responds to permission dialog
        store.onLocationPermissionResult(PermissionState.Granted)
        assertEquals(PermissionState.Granted, store.state.value.locationPermissionState)
    }

    @Test
    fun testNotificationPermissionFlow() {
        notGrantedPermissions.forEach { initialPermissionState ->
            // Mimics the user setting the notification permission previously
            store.onNotificationPermissionResult(initialPermissionState)
            assertEquals(false, store.state.value.shouldRequestNotificationPermissions)
            assertEquals(initialPermissionState, store.state.value.notificationPermissionState)
            // We request notification permission when user tries to enable alarm if not already granted
            store.onToggleAlarm()
            assertEquals(true, store.state.value.shouldRequestNotificationPermissions)
            // User responds to system permission dialog
            store.onNotificationPermissionResult(PermissionState.Granted)
            assertEquals(PermissionState.Granted, store.state.value.notificationPermissionState)
            assertEquals(false, store.state.value.shouldRequestNotificationPermissions)
        }
    }

    @Test
    fun onLocationChange_keepsTrackOfCurrentAndPreviousLocations() {
        assertEquals(null, store.state.value.usersLocation)
        assertEquals(emptyList(), store.state.value.usersLocationHistory)

        store.onLocationChange(listOf(someLocation))
        assertEquals(someLocation, store.state.value.usersLocation)
        assertEquals(listOf(LocationUpdate(Instant.fromEpochSeconds(0), someLocation)), store.state.value.usersLocationHistory)
        timeProvider.set(1)
        store.onLocationChange(listOf(someOtherLocation))
        assertEquals(someOtherLocation, store.state.value.usersLocation)
        // Location history is added to
        assertEquals(
            listOf(
                LocationUpdate(Instant.fromEpochSeconds(0), someLocation),
                LocationUpdate(Instant.fromEpochSeconds(1), someOtherLocation)
            ), store.state.value.usersLocationHistory
        )
    }

    @Test
    fun onLocationChange_updatesGeoFenceLocationToUserLocation() {
        // Follows when map hasn't been interacted yet
        assertEquals(false, store.state.value.mapInteracted)
        assertEquals(null, store.state.value.geoFenceLocation)
        store.onLocationChange(listOf(someLocation))
        assertEquals(someLocation, store.state.value.geoFenceLocation)
    }

    @Test
    fun onLocationChange_doesNotUpdateGeoFenceLocationToUserLocation() {
        store.onMapTap(someLocation)
        // Doesn't follow when map has been interacted with
        assertEquals(true, store.state.value.mapInteracted)
        assertEquals(someLocation, store.state.value.geoFenceLocation)
        store.onLocationChange(listOf(someOtherLocation))
        // Geofence location hasn't changed
        assertEquals(someLocation, store.state.value.geoFenceLocation)
    }

    @Test
    fun onLocationChange_updatesDistances() {
        assertEquals(null, store.state.value.usersLocation)
        assertEquals(null, store.state.value.geoFenceLocation)
        assertEquals(200, store.state.value.perimeterRadiusMeters)
        assertEquals(null, store.state.value.distanceToGeofencePerimeter)
        assertEquals(null, store.state.value.distanceToGeofence)
        store.onLocationChange(listOf(someLocation))
        // With default radius of 200m, and the same user and geofence location
        assertEquals(-200, store.state.value.distanceToGeofencePerimeter)
        assertEquals(0, store.state.value.distanceToGeofence)
    }

    @Test
    fun onLocationChange_updatesTriggeredState() {
        store.onMapTap(someLocation)
        store.onSetAlarm(true)
        assertEquals(false, store.state.value.alarmTriggered)
        assertEquals(null, store.state.value.usersLocation)
        assertEquals(someLocation, store.state.value.geoFenceLocation)
        // Location is updated to the same location as geofence, so alarm should trigger
        store.onLocationChange(listOf(someLocation))
        assertEquals(someLocation, store.state.value.usersLocation)
        assertEquals(someLocation, store.state.value.geoFenceLocation)
        assertEquals(true, store.state.value.alarmTriggered)
        // Location updated to outside geofence
        store.onLocationChange(listOf(someOtherLocation))
        assertEquals(someOtherLocation, store.state.value.usersLocation)
        assertEquals(someLocation, store.state.value.geoFenceLocation)
        assertEquals(false, store.state.value.alarmTriggered)
    }

    @Test
    fun onRadiusChanged_noUserLocationSet() {
        assertEquals(200, store.state.value.perimeterRadiusMeters)
        assertEquals(null, store.state.value.distanceToGeofencePerimeter)
        store.onRadiusChanged(250)
        assertEquals(250, store.state.value.perimeterRadiusMeters)
        assertEquals(null, store.state.value.distanceToGeofencePerimeter)
    }

    @Test
    fun onRadiusChanged_withUserLocationSet() {
        store.onLocationChange(listOf(someLocation))
        assertEquals(200, store.state.value.perimeterRadiusMeters)
        assertEquals(-200, store.state.value.distanceToGeofencePerimeter)
        store.onRadiusChanged(250)
        assertEquals(250, store.state.value.perimeterRadiusMeters)
        assertEquals(-250, store.state.value.distanceToGeofencePerimeter)
    }

    @Test
    fun onRadiusChanged_allowsMinRadius() {
        store.onRadiusChanged(10)
        assertEquals(10, store.state.value.perimeterRadiusMeters)
        store.onRadiusChanged(9)
        assertEquals(10, store.state.value.perimeterRadiusMeters)
        store.onRadiusChanged(-1)
        assertEquals(10, store.state.value.perimeterRadiusMeters)
    }

    @Test
    fun onMapTap_setsNewGeofence() {
        assertEquals(false, store.state.value.mapInteracted)
        assertEquals(null, store.state.value.geoFenceLocation)
        store.onMapTap(someLocation)
        assertEquals(true, store.state.value.mapInteracted)
        assertEquals(someLocation, store.state.value.geoFenceLocation)
    }

    @Test
    fun onMapTap_setsDistances() {
        store.onLocationChange(listOf(someLocation))
        assertEquals(0, store.state.value.distanceToGeofence)
        assertEquals(-200, store.state.value.distanceToGeofencePerimeter)
        store.onMapTap(someOtherLocation)
        assertEquals(7194, store.state.value.distanceToGeofence) // Checked with google maps measure distance
        assertEquals(6994, store.state.value.distanceToGeofencePerimeter)
    }

    @Test
    fun onMapTap_setsTriggeredState() {
        store.onLocationChange(listOf(someLocation))
        store.onMapTap(someOtherLocation)
        assertEquals(false, store.state.value.alarmTriggered)
        store.onSetAlarm(true)
        assertEquals(false, store.state.value.alarmTriggered)
        store.onMapTap(someLocation)
        assertEquals(true, store.state.value.alarmTriggered)
    }

    @Test
    fun onAppForegrounded_doesNotListenForLocationIfNotGranted() {
        store.onAppBackgrounded()
        assertEquals(false, store.state.value.appInForeground)
        assertEquals(PermissionState.Unchecked, store.state.value.locationPermissionState)
        assertEquals(false, store.state.value.shouldListenForLocationUpdates)
        store.onAppForegrounded()
        assertEquals(true, store.state.value.appInForeground)
        assertEquals(false, store.state.value.shouldListenForLocationUpdates)
    }

    @Test
    fun onAppForegrounded_listensForLocationIfGranted() {
        store.onAppBackgrounded()
        store.onLocationPermissionResult(PermissionState.Granted)
        assertEquals(false, store.state.value.appInForeground)
        assertEquals(PermissionState.Granted, store.state.value.locationPermissionState)
        assertEquals(false, store.state.value.shouldListenForLocationUpdates)
        store.onAppForegrounded()
        assertEquals(true, store.state.value.appInForeground)
        assertEquals(true, store.state.value.shouldListenForLocationUpdates)
    }

    @Test
    fun onAppBackgrounded_disablesLocationUpdatesWhenAlarmDisabled() {
        assertEquals(true, store.state.value.appInForeground)
        assertEquals(false, store.state.value.alarmEnabled)
        assertEquals(false, store.state.value.shouldListenForLocationUpdates)
        store.onAppBackgrounded()
        assertEquals(false, store.state.value.appInForeground)
        assertEquals(false, store.state.value.shouldListenForLocationUpdates)
    }

    @Test
    fun onAppBackgrounded_continuesLocationUpdatesWhenAlarmEnabled() {
        enableAlarm()
        assertEquals(true, store.state.value.appInForeground)
        assertEquals(true, store.state.value.alarmEnabled)
        assertEquals(true, store.state.value.shouldListenForLocationUpdates)
        store.onAppBackgrounded()
        assertEquals(false, store.state.value.appInForeground)
        assertEquals(true, store.state.value.shouldListenForLocationUpdates)
    }

    @Test
    fun onMapShown() {
        assertEquals(false, store.state.value.shouldListenForLocationUpdates)
        store.onMapShown()
        assertEquals(true, store.state.value.shouldListenForLocationUpdates)
    }

    @Test
    fun onMapNotShown_continuesLocationUpdatesWhenAlarmEnabled() {
        enableAlarm()
        assertEquals(true, store.state.value.alarmEnabled)
        assertEquals(true, store.state.value.shouldListenForLocationUpdates)
        store.onMapNotShown()
        assertEquals(true, store.state.value.shouldListenForLocationUpdates)
    }

    @Test
    fun testFlyToLocationFlow() {
        store.onLocationChange(listOf(someLocation))
        assertEquals(null, store.state.value.usersLocationToFlyTo)
        store.onTapLocationIcon()
        assertEquals(someLocation, store.state.value.usersLocationToFlyTo)
        store.onFinishFlyingToUsersLocation()
        assertEquals(null, store.state.value.usersLocationToFlyTo)
    }

    @Test
    fun onToggleAlarm_whenNotificationPermissionGranted() {
        store.onNotificationPermissionResult(true)
        assertEquals(PermissionState.Granted, store.state.value.notificationPermissionState)
        assertEquals(false, store.state.value.alarmEnabled)
        store.onToggleAlarm()
        assertEquals(true, store.state.value.alarmEnabled)
        store.onToggleAlarm()
        assertEquals(false, store.state.value.alarmEnabled)
    }

    @Test
    fun onToggleAlarm_whenNotificationPermissionNotGranted() {
        notGrantedPermissions.forEach { permission ->
            store.onNotificationPermissionResult(permission)
            assertEquals(permission, store.state.value.notificationPermissionState)
            assertEquals(false, store.state.value.alarmEnabled)
            store.onToggleAlarm()
            assertEquals(false, store.state.value.alarmEnabled)
            assertEquals(true, store.state.value.userRequestedAlarmEnable)
            assertEquals(true, store.state.value.shouldRequestNotificationPermissions)
        }
    }

    @Test
    fun onSetAlarm_setsAlarmEnabledState() {
        timeProvider.set(1)
        assertEquals(false, store.state.value.alarmEnabled)
        assertEquals(null, store.state.value.alarmEnabledAt)
        store.onSetAlarm(true)
        assertEquals(true, store.state.value.alarmEnabled)
        assertEquals(Instant.fromEpochSeconds(1L), store.state.value.alarmEnabledAt)
        timeProvider.set(2)
        store.onSetAlarm(false)
        assertEquals(false, store.state.value.alarmEnabled)
        // alarmEnabledAt only set when enabling alarm
        assertEquals(Instant.fromEpochSeconds(1L), store.state.value.alarmEnabledAt)
        store.onSetAlarm(true)
        assertEquals(true, store.state.value.alarmEnabled)
        assertEquals(Instant.fromEpochSeconds(2L), store.state.value.alarmEnabledAt)
    }

    @Test
    fun onSetAlarm_setsMapInteracted() {
        assertEquals(false, store.state.value.mapInteracted)
        store.onSetAlarm(true)
        assertEquals(true, store.state.value.mapInteracted)
    }

    @Test
    fun onSetAlarm_resetsUserRequestedAlarmEnable() {
        // Sets userRequestedAlarmEnable=true as notification permissions aren't granted
        store.onToggleAlarm()
        assertEquals(true, store.state.value.userRequestedAlarmEnable)
        store.onSetAlarm(true)
        assertEquals(false, store.state.value.userRequestedAlarmEnable)
    }

    @Test
    fun onSetAlarm_setsAlarmTriggered() {
        store.onLocationChange(listOf(someLocation))
        assertEquals(false, store.state.value.alarmTriggered)
        store.onSetAlarm(true)
        assertEquals(true, store.state.value.alarmTriggered)
        store.onSetAlarm(false)
        assertEquals(false, store.state.value.alarmTriggered)
    }

    /** Happy path flow for enabling the alarm*/
    private fun enableAlarm() {
        store.onLocationPermissionResult(PermissionState.Granted)
        store.onNotificationPermissionResult(PermissionState.Granted)
        store.onMapShown()
        store.onToggleAlarm()
    }
}