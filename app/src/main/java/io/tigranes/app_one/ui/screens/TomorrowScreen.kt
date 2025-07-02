package io.tigranes.app_one.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.*

@Composable
fun TomorrowScreen(
    modifier: Modifier = Modifier
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    
    val tomorrow = today.plus(DatePeriod(days = 1))
    
    DayScreen(
        day = "Tomorrow",
        date = tomorrow,
        modifier = modifier
    )
}