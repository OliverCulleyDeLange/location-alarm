package uk.co.oliverdelange.location_alarm

import android.app.Application
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import uk.co.oliverdelange.location_alarm.di.androidModule
import uk.co.oliverdelange.location_alarm.helpers.isDebug
import uk.co.oliverdelange.location_alarm.location.LocationService
import uk.co.oliverdelange.locationalarm.di.sharedModule
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.store.AppStateStore

class MainApplication : Application() {
    private val locationService: LocationService by inject()
    private val appStateStore: AppStateStore by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(sharedModule + androidModule)
        }
        appStateStore.setDebug(isDebug())
        SLog.d("Koin started")

        locationService.listenToStateAndListenForLocationUpdates()
    }
}