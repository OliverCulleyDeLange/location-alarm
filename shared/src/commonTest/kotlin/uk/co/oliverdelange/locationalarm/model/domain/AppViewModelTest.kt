import uk.co.oliverdelange.locationalarm.model.domain.AppViewModel
import kotlin.test.Test
import kotlin.test.assertEquals

class AppViewModelTest {

    @Test
    fun onTapAllowLocationPermissions_setsState() {
        val vm = AppViewModel()
        assertEquals(false, vm.state.value.shouldRequestLocationPermissions)
        vm.onTapAllowLocationPermissions()
        assertEquals(true, vm.state.value.shouldRequestLocationPermissions)
    }
}