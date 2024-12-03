package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@Composable
fun RadiusScrubber(radius: Int, modifier: Modifier = Modifier, onRadiusChange: (Int) -> Unit) {
    val dragState = rememberDraggableState { onRadiusChange(radius + it.roundToInt()) }
    Column(modifier.draggable(dragState, Orientation.Vertical)) {
        Text("Radius:")
        Text(radius.toString())
    }
}

@Preview
@Composable
fun Preview_RadiusScrubber() = RadiusScrubber(500) {}