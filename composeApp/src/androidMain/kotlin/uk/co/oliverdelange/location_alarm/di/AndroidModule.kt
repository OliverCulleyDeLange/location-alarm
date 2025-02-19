package uk.co.oliverdelange.location_alarm.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uk.co.oliverdelange.location_alarm.haptics.Vibrator
import uk.co.oliverdelange.location_alarm.location.FusedLocationService
import uk.co.oliverdelange.location_alarm.location.LocationService
import uk.co.oliverdelange.location_alarm.location.LocationStateListener
import uk.co.oliverdelange.locationalarm.model.ui.debug.DebugViewModel
import uk.co.oliverdelange.locationalarm.model.ui.location_permission_required.LocationPermissionRequiredViewModel
import uk.co.oliverdelange.locationalarm.model.ui.map.MapViewModel

val androidModule = module {
    viewModel { MapViewModel(get(), get()) }
    viewModel { LocationPermissionRequiredViewModel(get(), get()) }
    viewModel { DebugViewModel(get(), get(), get(), get()) }

    single { Vibrator(androidContext()) }
    single<LocationStateListener> { LocationStateListener(get(), get()) }
    single<LocationService> { FusedLocationService(androidContext(), get()) }
}