package io.tigranes.app_one.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Today : Screen(
        route = "today",
        title = "Today",
        icon = Icons.Default.CalendarToday
    )
    
    object Tomorrow : Screen(
        route = "tomorrow",
        title = "Tomorrow",
        icon = Icons.Default.CalendarViewDay
    )
    
    object Yesterday : Screen(
        route = "yesterday",
        title = "Yesterday",
        icon = Icons.Default.History
    )
    
    object Stats : Screen(
        route = "stats",
        title = "Stats",
        icon = Icons.Default.BarChart
    )
}

val bottomNavItems = listOf(
    Screen.Yesterday,
    Screen.Today,
    Screen.Tomorrow,
    Screen.Stats
)