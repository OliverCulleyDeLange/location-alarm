package uk.co.oliverdelange.location_alarm.screens.debug

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
object DebugScreenRoute

@Composable
fun DebugScreen() {
    Text("Debug screen")
}