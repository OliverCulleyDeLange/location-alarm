package uk.co.oliverdelange.location_alarm

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.test.runner.AndroidJUnitRunner

/** Referenced in app/build.gradle in android -> defaultConfig -> testInstrumentationRunner */
class TestAppJUnitRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        Log.i("TestAppJUnitRunner", "Creating new application for Espresso Testing")
        val testApp = "uk.co.oliverdelange.location_alarm.EspressoApplication"
        return super.newApplication(cl, testApp, context)
    }

    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        super.finish(resultCode, results)
    }
}