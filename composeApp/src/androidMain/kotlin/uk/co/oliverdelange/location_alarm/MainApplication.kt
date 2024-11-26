package uk.co.oliverdelange.location_alarm

import android.app.Application
import di.sharedModule
import logging.setupLogging
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(sharedModule)
        }
        setupLogging()
        Timber.d("Koin started")
    }
}