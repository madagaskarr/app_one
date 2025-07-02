package io.tigranes.app_one.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.tigranes.app_one.ui.components.TaskList
import io.tigranes.app_one.ui.viewmodels.TaskViewModel
import kotlinx.datetime.*

@Composable
fun YesterdayScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    
    val yesterday = today.minus(DatePeriod(days = 1))
    
    val tasks by viewModel.tasks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(yesterday) {
        viewModel.selectDate(yesterday)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        TaskList(
            tasks = tasks,
            onToggleComplete = { taskId ->
                viewModel.toggleTaskCompletion(taskId)
            },
            onMoveToTomorrow = { taskId ->
                viewModel.moveTaskToTomorrow(taskId)
            },
            onDelete = { task ->
                viewModel.deleteTask(task)
            },
            emptyMessage = "No tasks from yesterday.\nGreat job completing everything!"
        )
        
        // No FAB for yesterday screen - can't add tasks to the past
    }
    
    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar or toast here if needed
            viewModel.dismissUserMessage()
        }
    }
}