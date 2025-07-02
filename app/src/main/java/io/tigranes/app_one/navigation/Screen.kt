package io.tigranes.app_one.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Today : Screen(
        route = "today",
        title = "Today",
        icon = Icons.Default.Schedule
    )
    
    object Tomorrow : Screen(
        route = "tomorrow",
        title = "Tomorrow",
        icon = Icons.Default.Event
    )
    
    object Yesterday : Screen(
        route = "yesterday",
        title = "Yesterday",
        icon = Icons.Default.DateRange
    )
    
    object Stats : Screen(
        route = "stats",
        title = "Stats",
        icon = Icons.AutoMirrored.Filled.ShowChart
    )
    
    object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )
}

val bottomNavItems = listOf(
    Screen.Yesterday,
    Screen.Today,
    Screen.Tomorrow,
    Screen.Stats
)