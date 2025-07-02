# Commitment

*A minimal, offline‑first Android app that helps you honor your daily commitments across Life, Work, and Relationships.*

---

## 1. Product Overview

Commitment replaces overwhelming backlogs with a simple ritual:

* **Plan Today & Tomorrow** – Add tasks strictly for *today* or *tomorrow*.
* **Reflect on Yesterday** – See what carried over; decide, reschedule, or drop.
* **Categorize** – Every task belongs to **Life**, **Work**, or **Relationships**.
* **Rate Your Mood** – Log how you feel once per day (emoji scale 1‑5).
* **Track Consistency** – Visual stats show completion streaks & category balance.

No accounts, no servers—everything lives locally so you can focus on doing the work, not syncing it.

---

## 2. Key Features

| Feature                                                  | Description                                                                       |
| -------------------------------------------------------- | --------------------------------------------------------------------------------- |
| Today / Tomorrow / Yesterday boards                      | Swipeable Compose tabs; each lists tasks for that day.                            |
| Add Task FAB                                             | Opens bottom sheet to enter title & category for *Today* (default) or *Tomorrow*. |
| Midnight Rollover                                        | WorkManager job at 00:05 local:                                                   |
| • Unfinished *Today* → Yesterday                         |                                                                                   |
| • *Tomorrow* → Today                                     |                                                                                   |
| • Yesterday tasks remain for reflection until user acts. |                                                                                   |
| Completion & Defer                                       | Checkbox toggles `completed`; long‑press offers *Move to Tomorrow* or *Delete*.   |
| Mood Check‑in                                            | Once per calendar day; stores rating in `daily_mood` table.                       |
| Stats Screen                                             | Charts last 7 / 30 days: completion % per category, mood trend line.              |

---

## 3. Architecture

```
UI (Jetpack Compose)
│
├── ViewModels  (AndroidX Lifecycle, Hilt)
│   └── exposes StateFlow<TaskUiState>
│
├── Repositories
│   ├── TaskRepository
│   └── MoodRepository
│        ↳ use DAOs under the hood
│
└── Data Layer
    ├── Room DB (SQLite)
    │   ├── task        (Entity)
    │   └── daily_mood  (Entity)
    └── DataStore<Preferences> (user settings)
```

*Pattern:* Clean MVVM with one‑way data flow & Kotlin Coroutines/Flow.

---

## 4. Data Model

```kotlin
@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    @ColumnInfo(name = "category") val category: Category,
    @ColumnInfo(name = "due_date") val dueDate: LocalDate,  // today or tomorrow
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
    val completed: Boolean = false,
    val completedAt: Instant? = null
)

enum class Category { LIFE, WORK, RELATIONSHIPS }

@Entity(tableName = "daily_mood", primaryKeys = ["date"])
data class DailyMood(
    val date: LocalDate,
    val rating: Int        // 1 😣  – 5 😄
)
```

### DAOs (excerpt)

```kotlin
@Dao
interface TaskDao {
    @Query("SELECT * FROM task WHERE due_date = :date ORDER BY id DESC")
    fun observeTasks(date: LocalDate): Flow<List<Task>>

    @Insert suspend fun insert(task: Task): Long
    @Update suspend fun update(task: Task)
    @Delete suspend fun delete(task: Task)

    @Query("UPDATE task SET due_date = :newDate WHERE due_date = :oldDate AND completed = 0")
    suspend fun rollover(oldDate: LocalDate, newDate: LocalDate)
}
```

---

## 5. Midnight Rollover Logic

*Scheduled via* **WorkManager** with `ExistingPeriodicWorkPolicy.KEEP`.

1. `rollover(today, yesterday)` – push unfinished today → yesterday.
2. `rollover(tomorrow, today)` – promote tomorrow's plan.
3. Clear completed tasks older than 30 days (auto‑archive table).

---

## 6. Build & Setup

| Item             | Setting                                                                                               |
| ---------------- | ----------------------------------------------------------------------------------------------------- |
| **Min SDK**      | 26 (Android 8.0)                                                                                      |
| **Compile SDK**  | 35                                                                                                    |
| **Language**     | Kotlin 2.1, JVM 17                                                                                    |
| **Primary libs** | Jetpack Compose BOM, Room 2.6, Hilt, Kotlinx‑Datetime, Accompanist Pager, MPAndroidChart, WorkManager |

### Getting Started

```bash
$ git clone git@github.com:<you>/commitment.git
$ cd commitment
$ ./gradlew :app:installDebug
```

The project uses **Gradle Version Catalogs** (`libs.versions.toml`) for dependencies.

---

## 7. Testing Strategy

* **Unit tests** – DAO + Repository (JUnit5, Turbine, Robolectric).
* **Instrumentation** – Compose UI tests for key flows.
* **CI** – GitHub Actions workflow `android.yml` runs lint, detekt, tests.

---

## 8. Roadmap

* [ ] Widget: quick‑add task from home screen.
* [ ] Notifications: gentle evening reminder if tasks pending.
* [ ] Export/Import JSON backup.
* [ ] Apple Vision Pro port using Kotlin Multiplatform.

---

## 9. License

Apache 2.0
