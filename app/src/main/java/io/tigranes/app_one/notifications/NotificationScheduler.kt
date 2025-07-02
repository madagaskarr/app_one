package io.tigranes.app_one.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tigranes.app_one.data.preferences.PreferencesRepository
import io.tigranes.app_one.data.preferences.ReminderTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    init {
        // Schedule notifications based on current preferences
        scope.launch {
            updateNotificationSchedules()
        }
    }
    
    suspend fun updateNotificationSchedules() {
        val preferences = preferencesRepository.userPreferences.first()
        
        // Schedule or cancel daily reminder
        if (preferences.dailyReminderEnabled) {
            scheduleDailyReminder(preferences.dailyReminderTime)
        } else {
            cancelDailyReminder()
        }
        
        // Schedule or cancel mood check-in
        if (preferences.moodCheckInEnabled) {
            scheduleMoodCheckIn(preferences.moodCheckInTime)
        } else {
            cancelMoodCheckIn()
        }
    }
    
    private fun scheduleDailyReminder(time: ReminderTime) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_DAILY_REMINDER
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val calendar = getNextAlarmTime(time.hour, time.minute)
        
        scheduleRepeatingAlarm(pendingIntent, calendar.timeInMillis)
    }
    
    private fun scheduleMoodCheckIn(time: ReminderTime) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_MOOD_CHECK_IN
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            MOOD_CHECK_IN_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val calendar = getNextAlarmTime(time.hour, time.minute)
        
        scheduleRepeatingAlarm(pendingIntent, calendar.timeInMillis)
    }
    
    private fun scheduleRepeatingAlarm(pendingIntent: PendingIntent, triggerTimeMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                // Fall back to inexact alarm
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }
    
    private fun getNextAlarmTime(hour: Int, minute: Int): Calendar {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return calendar
    }
    
    private fun cancelDailyReminder() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
    
    private fun cancelMoodCheckIn() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            MOOD_CHECK_IN_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
    
    companion object {
        private const val DAILY_REMINDER_REQUEST_CODE = 2001
        private const val MOOD_CHECK_IN_REQUEST_CODE = 2002
    }
}