package di

import org.koin.dsl.module
import uk.co.oliverdelange.locationalarm.model.ui.debug.DebugViewModel
import uk.co.oliverdelange.locationalarm.model.ui.location_permission_required.LocationPermissionRequiredViewModel
import uk.co.oliverdelange.locationalarm.model.ui.map.MapViewModel

val iosModule = module {
    factory { MapViewModel(get(), get()) }
    factory { LocationPermissionRequiredViewModel(get(), get()) }
    factory { DebugViewModel(get(), get(), get(), get()) }
}