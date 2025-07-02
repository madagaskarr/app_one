package io.tigranes.app_one.data.dao

import androidx.room.*
import io.tigranes.app_one.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface TaskDao {
    @Query("SELECT * FROM task WHERE due_date = :date ORDER BY id DESC")
    fun observeTasks(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("UPDATE task SET due_date = :newDate WHERE due_date = :oldDate AND completed = 0")
    suspend fun rollover(oldDate: LocalDate, newDate: LocalDate)

    @Query("DELETE FROM task WHERE completed = 1 AND completedAt < :beforeDate")
    suspend fun deleteOldCompletedTasks(beforeDate: Long)

    @Query("SELECT * FROM task WHERE due_date = :date AND category = :category")
    fun observeTasksByCategory(date: LocalDate, category: String): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM task WHERE due_date = :date AND completed = 1")
    suspend fun getCompletedCount(date: LocalDate): Int

    @Query("SELECT COUNT(*) FROM task WHERE due_date = :date")
    suspend fun getTotalCount(date: LocalDate): Int
    
    @Query("SELECT * FROM task WHERE due_date BETWEEN :startDate AND :endDate")
    suspend fun getTasksInDateRange(startDate: LocalDate, endDate: LocalDate): List<Task>
    
    @Query("SELECT COUNT(*) FROM task WHERE due_date BETWEEN :startDate AND :endDate AND category = :category AND completed = 1")
    suspend fun getCompletedCountByCategory(startDate: LocalDate, endDate: LocalDate, category: String): Int
    
    @Query("SELECT COUNT(*) FROM task WHERE due_date BETWEEN :startDate AND :endDate AND category = :category")
    suspend fun getTotalCountByCategory(startDate: LocalDate, endDate: LocalDate, category: String): Int
    
    @Query("SELECT category, COUNT(*) as count FROM task WHERE due_date BETWEEN :startDate AND :endDate GROUP BY category")
    suspend fun getTaskCountsByCategory(startDate: LocalDate, endDate: LocalDate): List<CategoryCount>
    
    @Query("SELECT category, COUNT(*) as count FROM task WHERE due_date BETWEEN :startDate AND :endDate AND completed = 1 GROUP BY category")
    suspend fun getCompletedTaskCountsByCategory(startDate: LocalDate, endDate: LocalDate): List<CategoryCount>
}

data class CategoryCount(
    val category: String,
    val count: Int
)