package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme
import kotlin.math.roundToInt

@Composable
fun RadiusScrubber(radius: Int, modifier: Modifier = Modifier, onRadiusChange: (Int) -> Unit) {
    val dragState = rememberDraggableState { onRadiusChange(radius - it.roundToInt()) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UpArrowIcon()
        Column(
            modifier = Modifier
                .draggable(dragState, Orientation.Vertical)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Radius:",
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                radius.toString(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
            )
        }
        DownArrowIcon()
    }
}

@Composable
private fun UpArrowIcon() = ArrowIcon(90)

@Composable
private fun DownArrowIcon() = ArrowIcon(270)

@Composable
private fun ArrowIcon(rotate: Int) {
    Icon(
        Icons.AutoMirrored.Default.ArrowBack,
        null,
        Modifier.rotate(rotate.toFloat()),
        MaterialTheme.colorScheme.primaryContainer
    )
}

@PreviewLightDark
@Composable
fun Preview_RadiusScrubber() = AppTheme { RadiusScrubber(500) {} }
