package io.tigranes.app_one.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val DAILY_REMINDER_CHANNEL_ID = "daily_reminder"
    const val MOOD_CHECK_IN_CHANNEL_ID = "mood_check_in"
    
    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Daily Reminder Channel
            val dailyReminderChannel = NotificationChannel(
                DAILY_REMINDER_CHANNEL_ID,
                "Daily Task Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for your daily tasks"
                setShowBadge(true)
            }
            
            // Mood Check-in Channel
            val moodCheckInChannel = NotificationChannel(
                MOOD_CHECK_IN_CHANNEL_ID,
                "Mood Check-in Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to log your daily mood"
                setShowBadge(false)
            }
            
            notificationManager.createNotificationChannel(dailyReminderChannel)
            notificationManager.createNotificationChannel(moodCheckInChannel)
        }
    }
}