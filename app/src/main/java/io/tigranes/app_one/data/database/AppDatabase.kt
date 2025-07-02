package io.tigranes.app_one.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.tigranes.app_one.data.dao.MoodDao
import io.tigranes.app_one.data.dao.TaskDao
import io.tigranes.app_one.data.model.DailyMood
import io.tigranes.app_one.data.model.Task

@Database(
    entities = [Task::class, DailyMood::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun moodDao(): MoodDao

    companion object {
        const val DATABASE_NAME = "commitment_database"
    }
}