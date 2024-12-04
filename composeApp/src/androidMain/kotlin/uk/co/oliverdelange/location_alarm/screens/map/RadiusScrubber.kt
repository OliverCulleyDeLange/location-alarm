package uk.co.oliverdelange.location_alarm.screens.map

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uk.co.oliverdelange.location_alarm.ui.theme.AppTheme
import kotlin.math.roundToInt

@Composable
fun RadiusScrubber(radius: Int, modifier: Modifier = Modifier, onRadiusChange: (Int) -> Unit) {
    val dragState = rememberDraggableState { onRadiusChange(radius - it.roundToInt()) }
    val scope = rememberCoroutineScope()
    var radiusHelpAnimationKey by remember { mutableIntStateOf(0) }
    val offsetUp = remember { Animatable(0f) }
    val offsetDown = remember { Animatable(0f) }

    LaunchedEffect(radiusHelpAnimationKey) {
        if (radiusHelpAnimationKey > 0) scope.launch {
            offsetUp.animateTo(-300f, animationSpec = tween(durationMillis = 200))
            offsetUp.animateTo(0f, animationSpec = tween(durationMillis = 100))
            offsetDown.animateTo(300f, animationSpec = tween(durationMillis = 200))
            offsetDown.animateTo(0f, animationSpec = tween(durationMillis = 100))
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UpArrowIcon(Modifier.offset { IntOffset(0, offsetUp.value.toInt()) })
        Column(
            modifier = Modifier
                .draggable(dragState, Orientation.Vertical)
                .clickable {
                    radiusHelpAnimationKey++
                }
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
        DownArrowIcon(Modifier.offset { IntOffset(0, offsetDown.value.toInt()) })
    }
}

@Composable
private fun UpArrowIcon(modifier: Modifier = Modifier) = ArrowIcon(90, modifier)

@Composable
private fun DownArrowIcon(modifier: Modifier = Modifier) = ArrowIcon(270, modifier)

@Composable
private fun ArrowIcon(rotate: Int, modifier: Modifier = Modifier) {
    Icon(
        Icons.AutoMirrored.Default.ArrowBack,
        null,
        modifier
            .rotate(rotate.toFloat())
            .size(40.dp),
        MaterialTheme.colorScheme.primaryContainer
    )
}

@PreviewLightDark
@Composable
fun Preview_RadiusScrubber() = AppTheme { RadiusScrubber(500) {} }
