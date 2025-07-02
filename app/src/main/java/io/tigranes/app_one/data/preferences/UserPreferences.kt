package io.tigranes.app_one.data.preferences

import kotlinx.coroutines.flow.Flow

data class UserPreferences(
    val theme: Theme = Theme.SYSTEM,
    val dailyReminderEnabled: Boolean = true,
    val dailyReminderTime: ReminderTime = ReminderTime(9, 0), // 9:00 AM default
    val moodCheckInEnabled: Boolean = true,
    val moodCheckInTime: ReminderTime = ReminderTime(20, 0), // 8:00 PM default
    val autoDeleteCompletedTasks: Boolean = false,
    val autoDeleteDays: Int = 30,
    val showCompletedTasks: Boolean = true,
    val startWeekOnMonday: Boolean = true
)

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}

data class ReminderTime(
    val hour: Int,
    val minute: Int
) {
    fun toFormattedString(): String {
        val hourStr = if (hour == 0 || hour == 12) 12 else hour % 12
        val minuteStr = minute.toString().padStart(2, '0')
        val amPm = if (hour < 12) "AM" else "PM"
        return "$hourStr:$minuteStr $amPm"
    }
}

interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateTheme(theme: Theme)
    suspend fun updateDailyReminder(enabled: Boolean, time: ReminderTime? = null)
    suspend fun updateMoodCheckIn(enabled: Boolean, time: ReminderTime? = null)
    suspend fun updateAutoDelete(enabled: Boolean, days: Int? = null)
    suspend fun updateShowCompletedTasks(show: Boolean)
    suspend fun updateStartWeekOnMonday(startOnMonday: Boolean)
}