package uk.co.oliverdelange.location_alarm

import android.app.Application
import android.util.Log
import di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(sharedModule)
        }
        Log.d("OCD", "Koin started")
    }
}