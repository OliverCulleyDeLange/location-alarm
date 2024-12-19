import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import uk.co.oliverdelange.locationalarm.model.domain.AppViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    @BeforeTest
    fun before() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun onTapAllowLocationPermissions_setsState() {
        val vm = AppViewModel()
        assertEquals(false, vm.state.value.shouldRequestLocationPermissions)
        vm.onTapAllowLocationPermissions()
        assertEquals(true, vm.state.value.shouldRequestLocationPermissions)
    }
}