package io.tigranes.app_one.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tigranes.app_one.data.model.DailyMood
import io.tigranes.app_one.data.repository.MoodRepository
import io.tigranes.app_one.data.repository.TaskRepository
import io.tigranes.app_one.data.repository.TaskStatistics
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val moodRepository: MoodRepository
) : ViewModel() {
    
    private val _selectedPeriod = MutableStateFlow(StatsPeriod.WEEK)
    val selectedPeriod: StateFlow<StatsPeriod> = _selectedPeriod.asStateFlow()
    
    private val _statsUiState = MutableStateFlow(StatsUiState())
    val statsUiState: StateFlow<StatsUiState> = _statsUiState.asStateFlow()
    
    val recentMoods: StateFlow<List<DailyMood>> = _selectedPeriod
        .flatMapLatest { period ->
            val days = when (period) {
                StatsPeriod.WEEK -> 7
                StatsPeriod.MONTH -> 30
            }
            moodRepository.observeRecentMoods(days)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    init {
        loadStatistics()
    }
    
    fun selectPeriod(period: StatsPeriod) {
        _selectedPeriod.value = period
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                _statsUiState.update { it.copy(isLoading = true) }
                
                val days = when (_selectedPeriod.value) {
                    StatsPeriod.WEEK -> 7
                    StatsPeriod.MONTH -> 30
                }
                
                val today = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                
                val categoryStats = mutableMapOf<String, CategoryStats>()
                var totalCompleted = 0
                var totalTasks = 0
                
                // Calculate stats for each day
                for (i in 0 until days) {
                    val date = LocalDate(
                        today.year,
                        today.monthNumber,
                        today.dayOfMonth - i
                    )
                    
                    val stats = taskRepository.getTaskStatistics(date)
                    totalCompleted += stats.completedCount
                    totalTasks += stats.totalCount
                }
                
                val averageMood = moodRepository.getAverageMoodForLastDays(days)
                
                _statsUiState.update {
                    it.copy(
                        isLoading = false,
                        totalTasksCompleted = totalCompleted,
                        totalTasks = totalTasks,
                        averageCompletionRate = if (totalTasks > 0) {
                            totalCompleted.toFloat() / totalTasks
                        } else 0f,
                        averageMood = averageMood.toFloat(),
                        categoryStats = categoryStats
                    )
                }
            } catch (e: Exception) {
                _statsUiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load statistics: ${e.message}"
                    )
                }
            }
        }
    }
}

enum class StatsPeriod {
    WEEK,
    MONTH
}

data class StatsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalTasksCompleted: Int = 0,
    val totalTasks: Int = 0,
    val averageCompletionRate: Float = 0f,
    val averageMood: Float = 0f,
    val categoryStats: Map<String, CategoryStats> = emptyMap()
)

data class CategoryStats(
    val category: String,
    val completedCount: Int,
    val totalCount: Int,
    val completionRate: Float
)