package io.tigranes.app_one.data.backup

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val version: Int = 1,
    val exportDate: Instant,
    val tasks: List<TaskBackup>,
    val moods: List<MoodBackup>
)

@Serializable
data class TaskBackup(
    val title: String,
    val category: String,
    val dueDate: LocalDate,
    val completed: Boolean,
    val completedAt: Instant?
)

@Serializable
data class MoodBackup(
    val date: LocalDate,
    val rating: Int
)