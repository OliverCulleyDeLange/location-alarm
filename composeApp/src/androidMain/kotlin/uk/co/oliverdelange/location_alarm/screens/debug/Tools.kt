package uk.co.oliverdelange.location_alarm.screens.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Tools() {
    Column {
        Button(onClick = { throw RuntimeException("Test crash") }) {
            Text("Crash")
        }
    }
}