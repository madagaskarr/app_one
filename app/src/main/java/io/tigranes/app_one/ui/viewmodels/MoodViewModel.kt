package io.tigranes.app_one.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tigranes.app_one.data.model.DailyMood
import io.tigranes.app_one.data.repository.MoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val moodRepository: MoodRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MoodUiState())
    val uiState: StateFlow<MoodUiState> = _uiState.asStateFlow()
    
    init {
        checkTodaysMood()
    }
    
    private fun checkTodaysMood() {
        viewModelScope.launch {
            try {
                val todaysMood = moodRepository.getTodaysMood()
                _uiState.update {
                    it.copy(
                        todaysMood = todaysMood,
                        hasMoodForToday = todaysMood != null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to check today's mood: ${e.message}")
                }
            }
        }
    }
    
    fun recordMood(rating: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isRecording = true) }
                
                moodRepository.recordMood(rating)
                
                _uiState.update {
                    it.copy(
                        isRecording = false,
                        hasMoodForToday = true,
                        todaysMood = DailyMood(
                            date = Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date,
                            rating = rating
                        ),
                        showMoodDialog = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRecording = false,
                        error = "Failed to record mood: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun showMoodDialog() {
        _uiState.update { it.copy(showMoodDialog = true) }
    }
    
    fun dismissMoodDialog() {
        _uiState.update { it.copy(showMoodDialog = false) }
    }
    
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class MoodUiState(
    val isRecording: Boolean = false,
    val hasMoodForToday: Boolean = false,
    val todaysMood: DailyMood? = null,
    val showMoodDialog: Boolean = false,
    val error: String? = null
)