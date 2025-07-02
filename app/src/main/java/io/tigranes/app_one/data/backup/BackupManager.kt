package io.tigranes.app_one.data.backup

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tigranes.app_one.data.dao.TaskDao
import io.tigranes.app_one.data.dao.MoodDao
import io.tigranes.app_one.data.model.Category
import io.tigranes.app_one.data.model.DailyMood
import io.tigranes.app_one.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskDao: TaskDao,
    private val moodDao: MoodDao
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    suspend fun exportData(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Get all tasks from the database
            val tasks = getAllTasks()
            val taskBackups = tasks.map { task ->
                TaskBackup(
                    title = task.title,
                    category = task.category.name,
                    dueDate = task.dueDate,
                    completed = task.completed,
                    completedAt = task.completedAt
                )
            }
            
            // Get all moods from the database
            val moods = getAllMoods()
            val moodBackups = moods.map { mood ->
                MoodBackup(
                    date = mood.date,
                    rating = mood.rating
                )
            }
            
            // Create backup data
            val backupData = BackupData(
                version = 1,
                exportDate = Clock.System.now(),
                tasks = taskBackups,
                moods = moodBackups
            )
            
            // Write to file
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(json.encodeToString(backupData))
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun importData(uri: Uri): Result<ImportResult> = withContext(Dispatchers.IO) {
        try {
            // Read from file
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: return@withContext Result.failure(Exception("Could not read file"))
            
            // Parse backup data
            val backupData = json.decodeFromString<BackupData>(jsonString)
            
            // Import tasks
            var importedTasks = 0
            backupData.tasks.forEach { taskBackup ->
                try {
                    val task = Task(
                        title = taskBackup.title,
                        category = Category.valueOf(taskBackup.category),
                        dueDate = taskBackup.dueDate,
                        completed = taskBackup.completed,
                        completedAt = taskBackup.completedAt
                    )
                    taskDao.insert(task)
                    importedTasks++
                } catch (e: Exception) {
                    // Skip invalid tasks
                }
            }
            
            // Import moods
            var importedMoods = 0
            backupData.moods.forEach { moodBackup ->
                try {
                    val mood = DailyMood(
                        date = moodBackup.date,
                        rating = moodBackup.rating
                    )
                    moodDao.insertMood(mood)
                    importedMoods++
                } catch (e: Exception) {
                    // Skip invalid moods
                }
            }
            
            Result.success(
                ImportResult(
                    tasksImported = importedTasks,
                    moodsImported = importedMoods,
                    totalTasks = backupData.tasks.size,
                    totalMoods = backupData.moods.size
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        // Clear all tasks
        getAllTasks().forEach { task ->
            taskDao.delete(task)
        }
        
        // Clear all moods
        getAllMoods().forEach { mood ->
            moodDao.deleteMood(mood)
        }
    }
    
    private suspend fun getAllTasks(): List<Task> {
        // Get tasks for a wide date range
        val startDate = LocalDate(2020, 1, 1)
        val endDate = LocalDate(2030, 12, 31)
        return taskDao.getTasksInDateRange(startDate, endDate)
    }
    
    private suspend fun getAllMoods(): List<DailyMood> {
        // Get moods for a wide date range
        val startDate = LocalDate(2020, 1, 1)
        val endDate = LocalDate(2030, 12, 31)
        return moodDao.getMoodsInRange(startDate, endDate)
    }
}

data class ImportResult(
    val tasksImported: Int,
    val moodsImported: Int,
    val totalTasks: Int,
    val totalMoods: Int
)