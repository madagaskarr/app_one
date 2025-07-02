package io.tigranes.app_one.workers

import android.content.Context
import androidx.work.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.concurrent.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerInitializer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun initialize() {
        scheduleMidnightRollover()
    }
    
    private fun scheduleMidnightRollover() {
        val workManager = WorkManager.getInstance(context)
        
        // Calculate initial delay until 00:05 AM
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val currentDateTime = now.toLocalDateTime(timeZone)
        
        // Target time is 00:05 AM
        val targetTime = if (currentDateTime.hour >= 0 && currentDateTime.minute >= 5) {
            // If it's past 00:05 today, schedule for tomorrow
            currentDateTime.date.plus(DatePeriod(days = 1))
                .atTime(0, 5)
        } else {
            // Schedule for today at 00:05
            currentDateTime.date.atTime(0, 5)
        }
        
        val targetInstant = targetTime.toInstant(timeZone)
        val initialDelay = targetInstant.toEpochMilliseconds() - now.toEpochMilliseconds()
        
        // Create constraints
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        
        // Create the periodic work request (runs every 24 hours)
        val rolloverWork = PeriodicWorkRequestBuilder<MidnightRolloverWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(MidnightRolloverWorker.TAG)
            .build()
        
        // Enqueue the work with KEEP policy to prevent duplicates
        workManager.enqueueUniquePeriodicWork(
            MidnightRolloverWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            rolloverWork
        )
    }
    
    fun cancelAllWork() {
        WorkManager.getInstance(context).cancelAllWorkByTag(MidnightRolloverWorker.TAG)
    }
}