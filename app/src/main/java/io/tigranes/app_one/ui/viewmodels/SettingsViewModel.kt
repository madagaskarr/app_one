package io.tigranes.app_one.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tigranes.app_one.data.preferences.PreferencesRepository
import io.tigranes.app_one.data.preferences.ReminderTime
import io.tigranes.app_one.data.preferences.Theme
import io.tigranes.app_one.data.preferences.UserPreferences
import io.tigranes.app_one.notifications.NotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    
    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )
    
    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            preferencesRepository.updateTheme(theme)
        }
    }
    
    fun updateDailyReminder(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDailyReminder(enabled)
            notificationScheduler.updateNotificationSchedules()
        }
    }
    
    fun updateDailyReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDailyReminder(
                enabled = userPreferences.value.dailyReminderEnabled,
                time = ReminderTime(hour, minute)
            )
            notificationScheduler.updateNotificationSchedules()
        }
    }
    
    fun updateMoodCheckIn(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateMoodCheckIn(enabled)
            notificationScheduler.updateNotificationSchedules()
        }
    }
    
    fun updateMoodCheckInTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.updateMoodCheckIn(
                enabled = userPreferences.value.moodCheckInEnabled,
                time = ReminderTime(hour, minute)
            )
            notificationScheduler.updateNotificationSchedules()
        }
    }
    
    fun updateAutoDelete(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateAutoDelete(enabled)
        }
    }
    
    fun updateAutoDeleteDays(days: Int) {
        viewModelScope.launch {
            preferencesRepository.updateAutoDelete(
                enabled = userPreferences.value.autoDeleteCompletedTasks,
                days = days
            )
        }
    }
    
    fun updateShowCompletedTasks(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateShowCompletedTasks(show)
        }
    }
    
    fun updateStartWeekOnMonday(startOnMonday: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateStartWeekOnMonday(startOnMonday)
        }
    }
}