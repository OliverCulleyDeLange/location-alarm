@file:OptIn(ExperimentalCoroutinesApi::class)

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import uk.co.oliverdelange.location_alarm.location.LocationService
import uk.co.oliverdelange.location_alarm.location.LocationStateListener
import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.store.AppStateStore

class LocationStateListenerTest {

    private lateinit var locationListener: LocationStateListener
    private val mockAppStateStore = mockk<AppStateStore>(relaxed = true)
    private val mockLocationService = mockk<LocationService>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        locationListener = LocationStateListener(mockAppStateStore, mockLocationService)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `should start and stop listening based on state changes`() = testScope.runTest {
        val stateFlow = MutableStateFlow(AppState(shouldListenForLocationUpdates = false))
        every { mockAppStateStore.state } returns stateFlow

        locationListener.listenToStateAndListenForLocationUpdates()

        advanceUntilIdle()
        coVerify { mockLocationService.stopListeningForUpdates() }

        stateFlow.value = AppState(shouldListenForLocationUpdates = true)
        advanceUntilIdle()
        coVerify { mockLocationService.listenForUpdates() }

        stateFlow.value = AppState(shouldListenForLocationUpdates = false)
        advanceUntilIdle()
        coVerify(exactly = 2) { mockLocationService.stopListeningForUpdates() }

    }
}
