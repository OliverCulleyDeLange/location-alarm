package uk.co.oliverdelange.location_alarm.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import uk.co.oliverdelange.location_alarm.haptics.Vibrator
import uk.co.oliverdelange.location_alarm.resources.ApplicationStringProvider
import uk.co.oliverdelange.location_alarm.resources.StringProvider
import uk.co.oliverdelange.location_alarm.screens.MapUiViewModel

val androidModule = module {
    single<StringProvider> { ApplicationStringProvider(get()) }
    single { MapUiViewModel(get()) }
    single { Vibrator(androidContext()) }
}