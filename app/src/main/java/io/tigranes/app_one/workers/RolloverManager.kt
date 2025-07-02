package io.tigranes.app_one.workers

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tigranes.app_one.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RolloverManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskRepository: TaskRepository
) {
    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Triggers an immediate one-time rollover (useful for testing or manual trigger)
     */
    fun triggerImmediateRollover() {
        val rolloverWork = OneTimeWorkRequestBuilder<MidnightRolloverWorker>()
            .addTag("immediate_rollover")
            .build()
        
        workManager.enqueue(rolloverWork)
    }
    
    
    /**
     * Cancels all rollover work (both periodic and one-time)
     */
    fun cancelAllRolloverWork() {
        workManager.cancelUniqueWork(MidnightRolloverWorker.WORK_NAME)
        workManager.cancelAllWorkByTag("immediate_rollover")
    }
    
    /**
     * Gets the next scheduled rollover time
     */
    suspend fun getNextRolloverTime(): String {
        val workInfos = workManager.getWorkInfosForUniqueWork(MidnightRolloverWorker.WORK_NAME).await()
        val workInfo = workInfos.firstOrNull()
        
        return when (workInfo?.state) {
            WorkInfo.State.ENQUEUED -> "Scheduled for midnight"
            WorkInfo.State.RUNNING -> "Currently running"
            WorkInfo.State.SUCCEEDED -> "Completed, next run at midnight"
            WorkInfo.State.FAILED -> "Failed, will retry"
            WorkInfo.State.BLOCKED -> "Blocked, waiting for conditions"
            WorkInfo.State.CANCELLED -> "Cancelled"
            null -> "Not scheduled"
        }
    }
}