package io.tigranes.app_one.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.*

@Composable
fun YesterdayScreen(
    modifier: Modifier = Modifier
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    
    val yesterday = today.minus(DatePeriod(days = 1))
    
    DayScreen(
        day = "Yesterday",
        date = yesterday,
        modifier = modifier
    )
}