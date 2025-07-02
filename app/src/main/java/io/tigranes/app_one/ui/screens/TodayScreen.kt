package io.tigranes.app_one.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TodayScreen(
    modifier: Modifier = Modifier
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    
    DayScreen(
        day = "Today",
        date = today,
        modifier = modifier
    )
}