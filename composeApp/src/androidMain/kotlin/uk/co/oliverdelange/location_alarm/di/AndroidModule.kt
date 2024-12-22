package uk.co.oliverdelange.location_alarm.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import uk.co.oliverdelange.location_alarm.haptics.Vibrator
import uk.co.oliverdelange.location_alarm.location.LocationService
import uk.co.oliverdelange.location_alarm.screens.MapUiViewModel

val androidModule = module {
    single { MapUiViewModel(get(), get()) }
    single { Vibrator(androidContext()) }
    single { LocationService(androidContext(), get()) }
}