package uk.co.oliverdelange.location_alarm.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uk.co.oliverdelange.location_alarm.haptics.Vibrator
import uk.co.oliverdelange.location_alarm.location.FakeLocationService
import uk.co.oliverdelange.location_alarm.location.LocationService
import uk.co.oliverdelange.locationalarm.model.ui.debug.DebugViewModel
import uk.co.oliverdelange.locationalarm.model.ui.location_permission_required.LocationPermissionRequiredViewModel
import uk.co.oliverdelange.locationalarm.model.ui.map.MapViewModel

val testAndroidModule = module {
    viewModel { MapViewModel(get(), get()) }
    viewModel { LocationPermissionRequiredViewModel(get(), get()) }
    viewModel { DebugViewModel(get(), get(), get(), get()) }

    single { Vibrator(androidContext()) }
    single<LocationService> { FakeLocationService(get()) }
}