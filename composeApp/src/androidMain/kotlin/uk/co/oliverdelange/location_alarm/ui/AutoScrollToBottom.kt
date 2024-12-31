package uk.co.oliverdelange.location_alarm.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AutoScrollToBottom(items: Int, content: @Composable (LazyListState) -> Unit) {
    val scrollState = rememberLazyListState()
    var atBottomOfLogs by remember { mutableStateOf(true) }
    // If user is already at the bottom of the list, continue scrolling the the bottom when new items come through
    LaunchedEffect(items) {
        if (atBottomOfLogs) scrollState.animateScrollToItem(items - 1)
    }
    LaunchedEffect(scrollState.canScrollForward) {
        atBottomOfLogs = !scrollState.canScrollForward
    }
    content(scrollState)
}