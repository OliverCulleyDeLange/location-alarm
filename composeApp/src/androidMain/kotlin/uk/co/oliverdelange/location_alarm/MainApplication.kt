package uk.co.oliverdelange.location_alarm

import android.app.Application
import di.sharedModule
import logging.setupSharedLogging
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import uk.co.oliverdelange.location_alarm.di.androidModule

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(sharedModule + androidModule)
        }
        setupSharedLogging()
        Timber.plant(Timber.DebugTree())
        Timber.d("Koin started")
    }
}