package io.tigranes.app_one.data.dao

import androidx.room.*
import io.tigranes.app_one.data.model.DailyMood
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: DailyMood)

    @Query("SELECT * FROM daily_mood WHERE date = :date")
    suspend fun getMoodForDate(date: LocalDate): DailyMood?

    @Query("SELECT * FROM daily_mood WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun observeMoodsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyMood>>

    @Query("SELECT * FROM daily_mood ORDER BY date DESC LIMIT :limit")
    fun observeRecentMoods(limit: Int): Flow<List<DailyMood>>

    @Query("SELECT AVG(rating) FROM daily_mood WHERE date >= :startDate AND date <= :endDate")
    suspend fun getAverageMoodInRange(startDate: LocalDate, endDate: LocalDate): Double?

    @Delete
    suspend fun deleteMood(mood: DailyMood)
}