package io.tigranes.app_one.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.tigranes.app_one.data.preferences.Theme
import io.tigranes.app_one.ui.components.ThemeDialog
import io.tigranes.app_one.ui.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userPreferences by viewModel.userPreferences.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Appearance Section
        SettingsSection(title = "Appearance") {
            SettingsItem(
                icon = Icons.Default.Palette,
                title = "Theme",
                subtitle = when (userPreferences.theme) {
                    Theme.LIGHT -> "Light"
                    Theme.DARK -> "Dark"
                    Theme.SYSTEM -> "System default"
                },
                onClick = { showThemeDialog = true }
            )
        }
        
        HorizontalDivider()
        
        // Notifications Section
        SettingsSection(title = "Notifications") {
            SettingsSwitch(
                icon = Icons.Default.NotificationsActive,
                title = "Daily Reminder",
                subtitle = if (userPreferences.dailyReminderEnabled) {
                    "Remind me at ${userPreferences.dailyReminderTime.toFormattedString()}"
                } else {
                    "Get reminded about your daily tasks"
                },
                checked = userPreferences.dailyReminderEnabled,
                onCheckedChange = viewModel::updateDailyReminder
            )
            
            if (userPreferences.dailyReminderEnabled) {
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Reminder Time",
                    subtitle = userPreferences.dailyReminderTime.toFormattedString(),
                    onClick = { /* Show time picker */ }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsSwitch(
                icon = Icons.Default.Mood,
                title = "Mood Check-in Reminder",
                subtitle = if (userPreferences.moodCheckInEnabled) {
                    "Check in at ${userPreferences.moodCheckInTime.toFormattedString()}"
                } else {
                    "Get reminded to log your mood"
                },
                checked = userPreferences.moodCheckInEnabled,
                onCheckedChange = viewModel::updateMoodCheckIn
            )
            
            if (userPreferences.moodCheckInEnabled) {
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Check-in Time",
                    subtitle = userPreferences.moodCheckInTime.toFormattedString(),
                    onClick = { /* Show time picker */ }
                )
            }
        }
        
        HorizontalDivider()
        
        // Task Management Section
        SettingsSection(title = "Task Management") {
            SettingsSwitch(
                icon = Icons.Default.Visibility,
                title = "Show Completed Tasks",
                subtitle = "Display completed tasks in the task list",
                checked = userPreferences.showCompletedTasks,
                onCheckedChange = viewModel::updateShowCompletedTasks
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsSwitch(
                icon = Icons.Default.DeleteSweep,
                title = "Auto-delete Old Tasks",
                subtitle = if (userPreferences.autoDeleteCompletedTasks) {
                    "Delete completed tasks after ${userPreferences.autoDeleteDays} days"
                } else {
                    "Keep completed tasks forever"
                },
                checked = userPreferences.autoDeleteCompletedTasks,
                onCheckedChange = viewModel::updateAutoDelete
            )
            
            if (userPreferences.autoDeleteCompletedTasks) {
                SettingsItem(
                    icon = Icons.Default.Timer,
                    title = "Delete After",
                    subtitle = "${userPreferences.autoDeleteDays} days",
                    onClick = { /* Show days picker */ }
                )
            }
        }
        
        HorizontalDivider()
        
        // Calendar Section
        SettingsSection(title = "Calendar") {
            SettingsSwitch(
                icon = Icons.Default.CalendarMonth,
                title = "Start Week on Monday",
                subtitle = if (userPreferences.startWeekOnMonday) {
                    "Week starts on Monday"
                } else {
                    "Week starts on Sunday"
                },
                checked = userPreferences.startWeekOnMonday,
                onCheckedChange = viewModel::updateStartWeekOnMonday
            )
        }
        
        HorizontalDivider()
        
        // Data Section
        SettingsSection(title = "Data") {
            SettingsItem(
                icon = Icons.Default.CloudUpload,
                title = "Export Data",
                subtitle = "Save your data to a file",
                onClick = { /* Handle export */ }
            )
            
            SettingsItem(
                icon = Icons.Default.CloudDownload,
                title = "Import Data",
                subtitle = "Restore data from a file",
                onClick = { /* Handle import */ }
            )
            
            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Clear All Data",
                subtitle = "Delete all tasks and moods",
                onClick = { /* Show confirmation dialog */ }
            )
        }
        
        HorizontalDivider()
        
        // About Section
        SettingsSection(title = "About") {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Version",
                subtitle = "1.0.0",
                onClick = { }
            )
            
            SettingsItem(
                icon = Icons.Default.Code,
                title = "Open Source Licenses",
                subtitle = "View open source licenses",
                onClick = { /* Show licenses */ }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
    
    // Dialogs
    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = userPreferences.theme,
            onThemeSelected = viewModel::updateTheme,
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
private fun SettingsSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    )
}