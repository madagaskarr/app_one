package io.tigranes.app_one.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.tigranes.app_one.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*

@HiltWorker
class MidnightRolloverWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val now = Clock.System.now()
            val timeZone = TimeZone.currentSystemDefault()
            val today = now.toLocalDateTime(timeZone).date
            val yesterday = today.minus(DatePeriod(days = 1))
            val tomorrow = today.plus(DatePeriod(days = 1))
            
            // Perform the rollover operations
            taskRepository.performMidnightRollover(
                today = today,
                yesterday = yesterday,
                tomorrow = tomorrow
            )
            
            // Clean up old completed tasks (older than 30 days)
            taskRepository.cleanupOldCompletedTasks(daysToKeep = 30)
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
    
    companion object {
        const val WORK_NAME = "midnight_rollover_work"
        const val TAG = "MidnightRolloverWorker"
    }
}