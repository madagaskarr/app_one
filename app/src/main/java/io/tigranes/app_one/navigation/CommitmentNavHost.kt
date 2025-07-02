package io.tigranes.app_one.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.tigranes.app_one.ui.screens.*

@Composable
fun CommitmentNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Today.route,
        modifier = modifier
    ) {
        composable(Screen.Today.route) {
            TodayScreen()
        }
        composable(Screen.Tomorrow.route) {
            TomorrowScreen()
        }
        composable(Screen.Yesterday.route) {
            YesterdayScreen()
        }
        composable(Screen.Stats.route) {
            StatsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}