package uk.co.oliverdelange.location_alarm

import android.app.Application
import logging.setupSharedLogging
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.di.androidModule
import uk.co.oliverdelange.location_alarm.location.LocationService
import uk.co.oliverdelange.location_alarm.logging.CustomDebugTree
import uk.co.oliverdelange.locationalarm.di.sharedModule

class MainApplication : Application() {
    val locationService: LocationService by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(sharedModule + androidModule)
        }
        setupSharedLogging()
        Timber.plant(CustomDebugTree())
        Timber.d("Koin started")

        locationService.listenToStateAndListenForLocationUpdates()
    }
}