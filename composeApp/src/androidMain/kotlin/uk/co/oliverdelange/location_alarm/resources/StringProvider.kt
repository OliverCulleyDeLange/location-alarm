package uk.co.oliverdelange.location_alarm.resources

import android.content.Context

interface StringProvider {
    fun getString(resId: Int): String
}

class ApplicationStringProvider(val context: Context) : StringProvider {
    override fun getString(resId: Int) = context.getString(resId)
}