package io.tigranes.app_one.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.tigranes.app_one.navigation.CommitmentNavHost
import io.tigranes.app_one.navigation.Screen
import io.tigranes.app_one.navigation.bottomNavItems
import io.tigranes.app_one.ui.components.MoodCheckInDialog
import io.tigranes.app_one.ui.viewmodels.MoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentApp() {
    val navController = rememberNavController()
    val moodViewModel: MoodViewModel = hiltViewModel()
    val moodUiState by moodViewModel.uiState.collectAsState()
    
    // Check if we should show mood dialog when navigating to Today screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    LaunchedEffect(currentRoute) {
        if (currentRoute == Screen.Today.route && !moodUiState.hasMoodForToday) {
            moodViewModel.showMoodDialog()
        }
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = remember { SnackbarHostState() }
            )
        }
    ) { innerPadding ->
        CommitmentNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
    
    // Mood check-in dialog
    if (moodUiState.showMoodDialog) {
        MoodCheckInDialog(
            onDismiss = {
                moodViewModel.dismissMoodDialog()
            },
            onMoodSelected = { rating ->
                moodViewModel.recordMood(rating)
            }
        )
    }
}