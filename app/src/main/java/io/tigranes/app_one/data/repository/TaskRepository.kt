package io.tigranes.app_one.data.repository

import io.tigranes.app_one.data.dao.TaskDao
import io.tigranes.app_one.data.model.Category
import io.tigranes.app_one.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun observeTasksForDate(date: LocalDate): Flow<List<Task>> {
        return taskDao.observeTasks(date)
    }

    fun observeTasksByCategory(date: LocalDate, category: Category): Flow<List<Task>> {
        return taskDao.observeTasksByCategory(date, category.name)
    }

    suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)
    }

    suspend fun addTask(title: String, category: Category, dueDate: LocalDate): Long {
        val task = Task(
            title = title,
            category = category,
            dueDate = dueDate
        )
        return taskDao.insert(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

    suspend fun toggleTaskCompletion(taskId: Long) {
        val task = taskDao.getTaskById(taskId) ?: return
        val updatedTask = if (task.completed) {
            task.copy(completed = false, completedAt = null)
        } else {
            task.copy(completed = true, completedAt = Clock.System.now())
        }
        taskDao.update(updatedTask)
    }

    suspend fun moveTaskToTomorrow(taskId: Long) {
        val task = taskDao.getTaskById(taskId) ?: return
        val tomorrow = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .let { LocalDate(it.year, it.monthNumber, it.dayOfMonth + 1) }
        
        taskDao.update(task.copy(dueDate = tomorrow))
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    suspend fun performMidnightRollover(today: LocalDate, yesterday: LocalDate, tomorrow: LocalDate) {
        // Move incomplete today tasks to yesterday
        taskDao.rollover(today, yesterday)
        // Move tomorrow tasks to today
        taskDao.rollover(tomorrow, today)
    }

    suspend fun cleanupOldCompletedTasks(daysToKeep: Int = 30) {
        val cutoffDate = Clock.System.now().toEpochMilliseconds() - (daysToKeep * 24 * 60 * 60 * 1000L)
        taskDao.deleteOldCompletedTasks(cutoffDate)
    }

    suspend fun getTaskStatistics(date: LocalDate): TaskStatistics {
        val completedCount = taskDao.getCompletedCount(date)
        val totalCount = taskDao.getTotalCount(date)
        return TaskStatistics(
            completedCount = completedCount,
            totalCount = totalCount,
            completionRate = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
        )
    }
}

data class TaskStatistics(
    val completedCount: Int,
    val totalCount: Int,
    val completionRate: Float
)