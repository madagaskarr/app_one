package io.tigranes.app_one.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.tigranes.app_one.ui.components.AddTaskFab
import io.tigranes.app_one.ui.components.TaskList
import io.tigranes.app_one.ui.viewmodels.TaskViewModel
import kotlinx.datetime.*

@Composable
fun TomorrowScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    
    val tomorrow = today.plus(DatePeriod(days = 1))
    
    val tasks by viewModel.tasks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(tomorrow) {
        viewModel.selectDate(tomorrow)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        TaskList(
            tasks = tasks,
            onToggleComplete = { taskId ->
                viewModel.toggleTaskCompletion(taskId)
            },
            onMoveToTomorrow = { taskId ->
                // Can't move tomorrow's tasks to tomorrow
            },
            onDelete = { task ->
                viewModel.deleteTask(task)
            },
            emptyMessage = "No tasks planned for tomorrow.\nTap + to add one!"
        )
        
        AddTaskFab(
            onAddTask = { title, category, isForTomorrow ->
                // For tomorrow screen, we always add to tomorrow
                viewModel.addTask(title, category, true)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            defaultForTomorrow = true
        )
    }
    
    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar or toast here if needed
            viewModel.dismissUserMessage()
        }
    }
}