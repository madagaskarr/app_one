package io.tigranes.app_one.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tigranes.app_one.data.model.Category
import io.tigranes.app_one.data.model.Task
import io.tigranes.app_one.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    
    val tasks: StateFlow<List<Task>> = _selectedDate
        .filterNotNull()
        .flatMapLatest { date ->
            taskRepository.observeTasksForDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
    
    fun addTask(title: String, category: Category, isForTomorrow: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val date = _selectedDate.value ?: return@launch
                val targetDate = if (isForTomorrow) {
                    LocalDate(date.year, date.monthNumber, date.dayOfMonth + 1)
                } else {
                    date
                }
                
                taskRepository.addTask(
                    title = title.trim(),
                    category = category,
                    dueDate = targetDate
                )
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        userMessage = "Task added successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        userMessage = "Failed to add task: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun toggleTaskCompletion(taskId: Long) {
        viewModelScope.launch {
            try {
                taskRepository.toggleTaskCompletion(taskId)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(userMessage = "Failed to update task: ${e.message}")
                }
            }
        }
    }
    
    fun moveTaskToTomorrow(taskId: Long) {
        viewModelScope.launch {
            try {
                taskRepository.moveTaskToTomorrow(taskId)
                _uiState.update { 
                    it.copy(userMessage = "Task moved to tomorrow")
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(userMessage = "Failed to move task: ${e.message}")
                }
            }
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(task)
                _uiState.update { 
                    it.copy(userMessage = "Task deleted")
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(userMessage = "Failed to delete task: ${e.message}")
                }
            }
        }
    }
    
    fun dismissUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}

data class TaskUiState(
    val isLoading: Boolean = false,
    val userMessage: String? = null
)