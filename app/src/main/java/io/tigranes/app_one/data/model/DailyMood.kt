package io.tigranes.app_one.data.model

import androidx.room.Entity
import kotlinx.datetime.LocalDate

@Entity(tableName = "daily_mood", primaryKeys = ["date"])
data class DailyMood(
    val date: LocalDate,
    val rating: Int  // 1 to 5 scale
)