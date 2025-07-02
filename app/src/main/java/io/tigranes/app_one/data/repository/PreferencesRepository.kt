package io.tigranes.app_one.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                defaultTaskCategory = preferences[DEFAULT_TASK_CATEGORY] ?: "LIFE",
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: false,
                eveningReminderTime = preferences[EVENING_REMINDER_TIME] ?: "20:00",
                themeMode = preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name,
                hasCompletedOnboarding = preferences[HAS_COMPLETED_ONBOARDING] ?: false
            )
        }

    suspend fun updateDefaultTaskCategory(category: String) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_TASK_CATEGORY] = category
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateEveningReminderTime(time: String) {
        dataStore.edit { preferences ->
            preferences[EVENING_REMINDER_TIME] = time
        }
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.name
        }
    }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[HAS_COMPLETED_ONBOARDING] = true
        }
    }

    private companion object {
        val DEFAULT_TASK_CATEGORY = stringPreferencesKey("default_task_category")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val EVENING_REMINDER_TIME = stringPreferencesKey("evening_reminder_time")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }
}

data class UserPreferences(
    val defaultTaskCategory: String,
    val notificationsEnabled: Boolean,
    val eveningReminderTime: String,
    val themeMode: String,
    val hasCompletedOnboarding: Boolean
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}