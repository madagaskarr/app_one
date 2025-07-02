package io.tigranes.app_one.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import io.tigranes.app_one.MainActivity
import io.tigranes.app_one.R
import io.tigranes.app_one.data.repository.TaskRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var taskRepository: TaskRepository
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_DAILY_REMINDER -> showDailyReminder(context)
            ACTION_MOOD_CHECK_IN -> showMoodCheckInReminder(context)
        }
    }
    
    private fun showDailyReminder(context: Context) {
        if (!hasNotificationPermission(context)) return
        
        scope.launch {
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            
            val tasks = taskRepository.observeTasksForDate(today).first()
            val incompleteTasks = tasks.filter { !it.completed }
            
            if (incompleteTasks.isNotEmpty()) {
                val notification = NotificationCompat.Builder(context, NotificationChannels.DAILY_REMINDER_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Daily Tasks Reminder")
                    .setContentText("You have ${incompleteTasks.size} incomplete tasks for today")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(createPendingIntent(context))
                    .build()
                
                NotificationManagerCompat.from(context)
                    .notify(DAILY_REMINDER_NOTIFICATION_ID, notification)
            }
        }
    }
    
    private fun showMoodCheckInReminder(context: Context) {
        if (!hasNotificationPermission(context)) return
        
        val notification = NotificationCompat.Builder(context, NotificationChannels.MOOD_CHECK_IN_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Mood Check-in")
            .setContentText("How are you feeling today?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(createPendingIntent(context))
            .build()
        
        NotificationManagerCompat.from(context)
            .notify(MOOD_CHECK_IN_NOTIFICATION_ID, notification)
    }
    
    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    companion object {
        const val ACTION_DAILY_REMINDER = "io.tigranes.app_one.ACTION_DAILY_REMINDER"
        const val ACTION_MOOD_CHECK_IN = "io.tigranes.app_one.ACTION_MOOD_CHECK_IN"
        const val DAILY_REMINDER_NOTIFICATION_ID = 1001
        const val MOOD_CHECK_IN_NOTIFICATION_ID = 1002
    }
}