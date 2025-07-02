package io.tigranes.app_one.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesRepository {
    
    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val MOOD_CHECK_IN_ENABLED = booleanPreferencesKey("mood_check_in_enabled")
        val MOOD_CHECK_IN_HOUR = intPreferencesKey("mood_check_in_hour")
        val MOOD_CHECK_IN_MINUTE = intPreferencesKey("mood_check_in_minute")
        val AUTO_DELETE_ENABLED = booleanPreferencesKey("auto_delete_enabled")
        val AUTO_DELETE_DAYS = intPreferencesKey("auto_delete_days")
        val SHOW_COMPLETED_TASKS = booleanPreferencesKey("show_completed_tasks")
        val START_WEEK_ON_MONDAY = booleanPreferencesKey("start_week_on_monday")
    }
    
    override val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                theme = Theme.valueOf(
                    preferences[PreferencesKeys.THEME] ?: Theme.SYSTEM.name
                ),
                dailyReminderEnabled = preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] ?: true,
                dailyReminderTime = ReminderTime(
                    hour = preferences[PreferencesKeys.DAILY_REMINDER_HOUR] ?: 9,
                    minute = preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] ?: 0
                ),
                moodCheckInEnabled = preferences[PreferencesKeys.MOOD_CHECK_IN_ENABLED] ?: true,
                moodCheckInTime = ReminderTime(
                    hour = preferences[PreferencesKeys.MOOD_CHECK_IN_HOUR] ?: 20,
                    minute = preferences[PreferencesKeys.MOOD_CHECK_IN_MINUTE] ?: 0
                ),
                autoDeleteCompletedTasks = preferences[PreferencesKeys.AUTO_DELETE_ENABLED] ?: false,
                autoDeleteDays = preferences[PreferencesKeys.AUTO_DELETE_DAYS] ?: 30,
                showCompletedTasks = preferences[PreferencesKeys.SHOW_COMPLETED_TASKS] ?: true,
                startWeekOnMonday = preferences[PreferencesKeys.START_WEEK_ON_MONDAY] ?: true
            )
        }
    
    override suspend fun updateTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }
    
    override suspend fun updateDailyReminder(enabled: Boolean, time: ReminderTime?) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] = enabled
            time?.let {
                preferences[PreferencesKeys.DAILY_REMINDER_HOUR] = it.hour
                preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] = it.minute
            }
        }
    }
    
    override suspend fun updateMoodCheckIn(enabled: Boolean, time: ReminderTime?) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MOOD_CHECK_IN_ENABLED] = enabled
            time?.let {
                preferences[PreferencesKeys.MOOD_CHECK_IN_HOUR] = it.hour
                preferences[PreferencesKeys.MOOD_CHECK_IN_MINUTE] = it.minute
            }
        }
    }
    
    override suspend fun updateAutoDelete(enabled: Boolean, days: Int?) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_DELETE_ENABLED] = enabled
            days?.let {
                preferences[PreferencesKeys.AUTO_DELETE_DAYS] = it
            }
        }
    }
    
    override suspend fun updateShowCompletedTasks(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_COMPLETED_TASKS] = show
        }
    }
    
    override suspend fun updateStartWeekOnMonday(startOnMonday: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.START_WEEK_ON_MONDAY] = startOnMonday
        }
    }
}