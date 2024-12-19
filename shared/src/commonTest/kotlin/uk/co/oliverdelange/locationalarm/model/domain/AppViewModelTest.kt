import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import uk.co.oliverdelange.locationalarm.model.domain.AppViewModel
import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState
import uk.co.oliverdelange.locationalarm.provider.MockTimeProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private lateinit var timeProvider: MockTimeProvider

    private lateinit var vm: AppViewModel
    private val someLocation = Location(51.49434994606447, 0.04302039031141709) // The reach
    private val someOtherLocation = Location(51.49332877322526, -0.0607650466109374) // The arch
    private val notGrantedPermissions = listOf(PermissionState.Unknown, PermissionState.Denied(true), PermissionState.Denied(false))

    @BeforeTest
    fun before() {
        Dispatchers.setMain(StandardTestDispatcher())
        timeProvider = MockTimeProvider()
        vm = AppViewModel(timeProvider)
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLocationPermissionFLow() {
        assertEquals(false, vm.state.value.shouldRequestLocationPermissions)
        // User taps allow button on our landing screen
        vm.onTapAllowLocationPermissions()
        assertEquals(true, vm.state.value.shouldRequestLocationPermissions)
        // We have requested location permissions from the system
        vm.onRequestedLocationPermissions()
        assertEquals(false, vm.state.value.shouldRequestLocationPermissions)
        assertEquals(PermissionState.Unknown, vm.state.value.locationPermissionState)
        // User responds to permission dialog
        vm.onLocationPermissionResult(PermissionState.Granted)
        assertEquals(PermissionState.Granted, vm.state.value.locationPermissionState)
    }

    @Test
    fun testNotificationPermissionFlow() {
        notGrantedPermissions.forEach { initialPermissionState ->
            // Mimics the user setting the notification permission previously
            vm.onNotificationPermissionResult(initialPermissionState)
            assertEquals(false, vm.state.value.shouldRequestNotificationPermissions)
            assertEquals(initialPermissionState, vm.state.value.notificationPermissionState)
            // We request notification permission when user tries to enable alarm if not already granted
            vm.onToggleAlarm()
            assertEquals(true, vm.state.value.shouldRequestNotificationPermissions)
            // User responds to system permission dialog
            vm.onNotificationPermissionResult(PermissionState.Granted)
            assertEquals(PermissionState.Granted, vm.state.value.notificationPermissionState)
            assertEquals(false, vm.state.value.shouldRequestNotificationPermissions)
        }
    }

    @Test
    fun onLocationChange_keepsTrackOfCurrentAndPreviousLocations() {
        assertEquals(null, vm.state.value.usersLocation)
        assertEquals(emptyList(), vm.state.value.usersLocationHistory)

        vm.onLocationChange(listOf(someLocation))
        assertEquals(someLocation, vm.state.value.usersLocation)
        assertEquals(listOf(someLocation), vm.state.value.usersLocationHistory)

        vm.onLocationChange(listOf(someOtherLocation))
        assertEquals(someOtherLocation, vm.state.value.usersLocation)
        // Location history is added to
        assertEquals(listOf(someLocation, someOtherLocation), vm.state.value.usersLocationHistory)
    }

    @Test
    fun onLocationChange_updatesGeoFenceLocationToUserLocation() {
        // Follows when map hasn't been interacted yet
        assertEquals(false, vm.state.value.mapInteracted)
        assertEquals(null, vm.state.value.geoFenceLocation)
        vm.onLocationChange(listOf(someLocation))
        assertEquals(someLocation, vm.state.value.geoFenceLocation)
    }

    @Test
    fun onLocationChange_doesNotUpdateGeoFenceLocationToUserLocation() {
        vm.onMapTap(someLocation)
        // Doesn't follow when map has been interacted with
        assertEquals(true, vm.state.value.mapInteracted)
        assertEquals(someLocation, vm.state.value.geoFenceLocation)
        vm.onLocationChange(listOf(someOtherLocation))
        // Geofence location hasn't changed
        assertEquals(someLocation, vm.state.value.geoFenceLocation)
    }

    @Test
    fun onLocationChange_updatesDistances() {
        assertEquals(null, vm.state.value.usersLocation)
        assertEquals(null, vm.state.value.geoFenceLocation)
        assertEquals(200, vm.state.value.perimeterRadiusMeters)
        assertEquals(null, vm.state.value.distanceToGeofencePerimeter)
        assertEquals(null, vm.state.value.distanceToGeofence)
        vm.onLocationChange(listOf(someLocation))
        // With default radius of 200m, and the same user and geofence location
        assertEquals(-200, vm.state.value.distanceToGeofencePerimeter)
        assertEquals(0, vm.state.value.distanceToGeofence)
    }

    @Test
    fun onLocationChange_updatesTriggeredState() {
        vm.onMapTap(someLocation)
        vm.onSetAlarm(true)
        assertEquals(false, vm.state.value.alarmTriggered)
        assertEquals(null, vm.state.value.usersLocation)
        assertEquals(someLocation, vm.state.value.geoFenceLocation)
        // Location is updated to the same location as geofence, so alarm should trigger
        vm.onLocationChange(listOf(someLocation))
        assertEquals(someLocation, vm.state.value.usersLocation)
        assertEquals(someLocation, vm.state.value.geoFenceLocation)
        assertEquals(true, vm.state.value.alarmTriggered)
        // Location updated to outside geofence
        vm.onLocationChange(listOf(someOtherLocation))
        assertEquals(someOtherLocation, vm.state.value.usersLocation)
        assertEquals(someLocation, vm.state.value.geoFenceLocation)
        assertEquals(false, vm.state.value.alarmTriggered)
    }

    @Test
    fun onRadiusChanged_noUserLocationSet() {
        assertEquals(200, vm.state.value.perimeterRadiusMeters)
        assertEquals(null, vm.state.value.distanceToGeofencePerimeter)
        vm.onRadiusChanged(250)
        assertEquals(250, vm.state.value.perimeterRadiusMeters)
        assertEquals(null, vm.state.value.distanceToGeofencePerimeter)
    }

    @Test
    fun onRadiusChanged_withUserLocationSet() {
        vm.onLocationChange(listOf(someLocation))
        assertEquals(200, vm.state.value.perimeterRadiusMeters)
        assertEquals(-200, vm.state.value.distanceToGeofencePerimeter)
        vm.onRadiusChanged(250)
        assertEquals(250, vm.state.value.perimeterRadiusMeters)
        assertEquals(-250, vm.state.value.distanceToGeofencePerimeter)
    }

    @Test
    fun onRadiusChanged_allowsMinRadius() {
        vm.onRadiusChanged(10)
        assertEquals(10, vm.state.value.perimeterRadiusMeters)
        vm.onRadiusChanged(9)
        assertEquals(10, vm.state.value.perimeterRadiusMeters)
        vm.onRadiusChanged(-1)
        assertEquals(10, vm.state.value.perimeterRadiusMeters)
    }

    @Test
    fun onMapTap_setsNewGeofence() {
        assertEquals(false, vm.state.value.mapInteracted)
        assertEquals(null, vm.state.value.geoFenceLocation)
        vm.onMapTap(someLocation)
        assertEquals(true, vm.state.value.mapInteracted)
        assertEquals(someLocation, vm.state.value.geoFenceLocation)
    }

    @Test
    fun onMapTap_setsDistances() {
        vm.onLocationChange(listOf(someLocation))
        assertEquals(0, vm.state.value.distanceToGeofence)
        assertEquals(-200, vm.state.value.distanceToGeofencePerimeter)
        vm.onMapTap(someOtherLocation)
        assertEquals(7194, vm.state.value.distanceToGeofence) // Checked with google maps measure distance
        assertEquals(6994, vm.state.value.distanceToGeofencePerimeter)
    }

    @Test
    fun testFlyToLocationFlow() {
        vm.onLocationChange(listOf(someLocation))
        assertEquals(null, vm.state.value.usersLocationToFlyTo)
        vm.onTapLocationIcon()
        assertEquals(someLocation, vm.state.value.usersLocationToFlyTo)
        vm.onFinishFlyingToUsersLocation()
        assertEquals(null, vm.state.value.usersLocationToFlyTo)
    }

    @Test
    fun onToggleAlarm_whenNotificationPermissionGranted() {
        vm.onNotificationPermissionResult(true)
        assertEquals(PermissionState.Granted, vm.state.value.notificationPermissionState)
        assertEquals(false, vm.state.value.alarmEnabled)
        vm.onToggleAlarm()
        assertEquals(true, vm.state.value.alarmEnabled)
        vm.onToggleAlarm()
        assertEquals(false, vm.state.value.alarmEnabled)
    }

    @Test
    fun onToggleAlarm_whenNotificationPermissionNotGranted() {
        notGrantedPermissions.forEach { permission ->
            vm.onNotificationPermissionResult(permission)
            assertEquals(permission, vm.state.value.notificationPermissionState)
            assertEquals(false, vm.state.value.alarmEnabled)
            vm.onToggleAlarm()
            assertEquals(false, vm.state.value.alarmEnabled)
            assertEquals(true, vm.state.value.userRequestedAlarmEnable)
            assertEquals(true, vm.state.value.shouldRequestNotificationPermissions)
        }
    }

    @Test
    fun onSetAlarm_setsAlarmEnabledState() {
        timeProvider.set(1)
        assertEquals(false, vm.state.value.alarmEnabled)
        assertEquals(null, vm.state.value.alarmEnabledAt)
        vm.onSetAlarm(true)
        assertEquals(true, vm.state.value.alarmEnabled)
        assertEquals(Instant.fromEpochSeconds(1L), vm.state.value.alarmEnabledAt)
        timeProvider.set(2)
        vm.onSetAlarm(false)
        assertEquals(false, vm.state.value.alarmEnabled)
        // alarmEnabledAt only set when enabling alarm
        assertEquals(Instant.fromEpochSeconds(1L), vm.state.value.alarmEnabledAt)
        vm.onSetAlarm(true)
        assertEquals(true, vm.state.value.alarmEnabled)
        assertEquals(Instant.fromEpochSeconds(2L), vm.state.value.alarmEnabledAt)
    }

    @Test
    fun onSetAlarm_setsMapInteracted() {
        assertEquals(false, vm.state.value.mapInteracted)
        vm.onSetAlarm(true)
        assertEquals(true, vm.state.value.mapInteracted)
    }

    @Test
    fun onSetAlarm_resetsUserRequestedAlarmEnable() {
        // Sets userRequestedAlarmEnable=true as notification permissions aren't granted
        vm.onToggleAlarm()
        assertEquals(true, vm.state.value.userRequestedAlarmEnable)
        vm.onSetAlarm(true)
        assertEquals(false, vm.state.value.userRequestedAlarmEnable)
    }

    @Test
    fun onSetAlarm_setsAlarmTriggered() {
        vm.onLocationChange(listOf(someLocation))
        assertEquals(false, vm.state.value.alarmTriggered)
        vm.onSetAlarm(true)
        assertEquals(true, vm.state.value.alarmTriggered)
        vm.onSetAlarm(false)
        assertEquals(false, vm.state.value.alarmTriggered)
    }
}