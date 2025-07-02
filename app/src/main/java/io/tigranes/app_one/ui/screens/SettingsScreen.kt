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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.hilt.navigation.compose.hiltViewModel
import io.tigranes.app_one.data.preferences.Theme
import io.tigranes.app_one.ui.components.*
import io.tigranes.app_one.ui.viewmodels.BackupViewModel
import io.tigranes.app_one.ui.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    backupViewModel: BackupViewModel = hiltViewModel()
) {
    val userPreferences by settingsViewModel.userPreferences.collectAsState()
    val backupUiState by backupViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showDailyReminderTimePicker by remember { mutableStateOf(false) }
    var showMoodCheckInTimePicker by remember { mutableStateOf(false) }
    
    // File pickers
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { backupViewModel.exportData(it) }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { backupViewModel.importData(it) }
    }
    
    // Show snackbar messages
    LaunchedEffect(backupUiState) {
        backupUiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            backupViewModel.dismissMessage()
        }
        backupUiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            backupViewModel.dismissMessage()
        }
    }
    
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
                onCheckedChange = settingsViewModel::updateDailyReminder
            )
            
            if (userPreferences.dailyReminderEnabled) {
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Reminder Time",
                    subtitle = userPreferences.dailyReminderTime.toFormattedString(),
                    onClick = { showDailyReminderTimePicker = true }
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
                onCheckedChange = settingsViewModel::updateMoodCheckIn
            )
            
            if (userPreferences.moodCheckInEnabled) {
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Check-in Time",
                    subtitle = userPreferences.moodCheckInTime.toFormattedString(),
                    onClick = { showMoodCheckInTimePicker = true }
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
                onCheckedChange = settingsViewModel::updateShowCompletedTasks
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
                onCheckedChange = settingsViewModel::updateAutoDelete
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
                onCheckedChange = settingsViewModel::updateStartWeekOnMonday
            )
        }
        
        HorizontalDivider()
        
        // Data Section
        SettingsSection(title = "Data") {
            SettingsItem(
                icon = Icons.Default.CloudUpload,
                title = "Export Data",
                subtitle = "Save your data to a file",
                onClick = { showExportDialog = true }
            )
            
            SettingsItem(
                icon = Icons.Default.CloudDownload,
                title = "Import Data",
                subtitle = "Restore data from a file",
                onClick = { showImportDialog = true }
            )
            
            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Clear All Data",
                subtitle = "Delete all tasks and moods",
                onClick = { showClearDataDialog = true }
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
            onThemeSelected = settingsViewModel::updateTheme,
            onDismiss = { showThemeDialog = false }
        )
    }
    
    if (showExportDialog) {
        ExportConfirmationDialog(
            onConfirm = {
                showExportDialog = false
                val timestamp = Clock.System.now().epochSeconds
                exportLauncher.launch("commitment_backup_$timestamp.json")
            },
            onDismiss = { showExportDialog = false }
        )
    }
    
    if (showImportDialog) {
        ImportConfirmationDialog(
            onConfirm = {
                showImportDialog = false
                importLauncher.launch(arrayOf("application/json"))
            },
            onDismiss = { showImportDialog = false }
        )
    }
    
    if (showClearDataDialog) {
        ClearDataConfirmationDialog(
            onConfirm = {
                showClearDataDialog = false
                backupViewModel.clearAllData()
            },
            onDismiss = { showClearDataDialog = false }
        )
    }
    
    if (showDailyReminderTimePicker) {
        TimePickerDialog(
            initialHour = userPreferences.dailyReminderTime.hour,
            initialMinute = userPreferences.dailyReminderTime.minute,
            onTimeSelected = { hour, minute ->
                settingsViewModel.updateDailyReminderTime(hour, minute)
            },
            onDismiss = { showDailyReminderTimePicker = false }
        )
    }
    
    if (showMoodCheckInTimePicker) {
        TimePickerDialog(
            initialHour = userPreferences.moodCheckInTime.hour,
            initialMinute = userPreferences.moodCheckInTime.minute,
            onTimeSelected = { hour, minute ->
                settingsViewModel.updateMoodCheckInTime(hour, minute)
            },
            onDismiss = { showMoodCheckInTimePicker = false }
        )
    }
    
    SnackbarHost(hostState = snackbarHostState)
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