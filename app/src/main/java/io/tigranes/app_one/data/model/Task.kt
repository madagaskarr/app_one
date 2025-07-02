package io.tigranes.app_one.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    @ColumnInfo(name = "category")
    val category: Category,
    @ColumnInfo(name = "due_date")
    val dueDate: LocalDate,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = kotlinx.datetime.Clock.System.now(),
    val completed: Boolean = false,
    val completedAt: Instant? = null
)