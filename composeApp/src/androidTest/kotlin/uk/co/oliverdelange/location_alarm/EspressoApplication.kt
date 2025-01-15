package uk.co.oliverdelange.location_alarm

import android.app.Application
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import uk.co.oliverdelange.location_alarm.di.testAndroidModule
import uk.co.oliverdelange.locationalarm.di.sharedModule
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.store.AppStateStore

/** References in TestAppJUnitRunner */
class EspressoApplication : Application() {
    private val appStateStore: AppStateStore by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@EspressoApplication)
            modules(sharedModule + testAndroidModule)
        }
        appStateStore.setDebug(true)
        SLog.d("Test Koin started")
    }
}

