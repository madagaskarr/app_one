package io.tigranes.app_one.data.repository

import io.tigranes.app_one.data.dao.MoodDao
import io.tigranes.app_one.data.model.DailyMood
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepository @Inject constructor(
    private val moodDao: MoodDao
) {
    suspend fun recordMood(rating: Int) {
        require(rating in 1..5) { "Mood rating must be between 1 and 5" }
        
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        
        val mood = DailyMood(
            date = today,
            rating = rating
        )
        
        moodDao.insertMood(mood)
    }

    suspend fun getTodaysMood(): DailyMood? {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        
        return moodDao.getMoodForDate(today)
    }

    suspend fun hasMoodForToday(): Boolean {
        return getTodaysMood() != null
    }

    fun observeMoodsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyMood>> {
        return moodDao.observeMoodsInRange(startDate, endDate)
    }

    fun observeRecentMoods(days: Int = 7): Flow<List<DailyMood>> {
        return moodDao.observeRecentMoods(days)
    }

    suspend fun getAverageMoodInRange(startDate: LocalDate, endDate: LocalDate): Double {
        return moodDao.getAverageMoodInRange(startDate, endDate) ?: 0.0
    }

    suspend fun getAverageMoodForLastDays(days: Int): Double {
        val endDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        
        val startDate = LocalDate(
            endDate.year,
            endDate.monthNumber,
            endDate.dayOfMonth - (days - 1)
        )
        
        return getAverageMoodInRange(startDate, endDate)
    }

    suspend fun getMoodStatistics(days: Int = 30): MoodStatistics {
        val moods = mutableListOf<DailyMood>()
        val endDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        
        val startDate = LocalDate(
            endDate.year,
            endDate.monthNumber,
            endDate.dayOfMonth - (days - 1)
        )
        
        moodDao.observeMoodsInRange(startDate, endDate).collect { moodList ->
            moods.clear()
            moods.addAll(moodList)
        }
        
        return MoodStatistics(
            averageRating = moods.map { it.rating }.average().toFloat(),
            totalEntries = moods.size,
            moodDistribution = moods.groupBy { it.rating }.mapValues { it.value.size }
        )
    }
}

data class MoodStatistics(
    val averageRating: Float,
    val totalEntries: Int,
    val moodDistribution: Map<Int, Int>
)