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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TodayScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    
    val tasks by viewModel.tasks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(today) {
        viewModel.selectDate(today)
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
            emptyMessage = "No tasks for today.\nTap + to add one!"
        )
        
        AddTaskFab(
            onAddTask = { title, category, isForTomorrow ->
                viewModel.addTask(title, category, isForTomorrow)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            defaultForTomorrow = false
        )
    }
    
    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar or toast here if needed
            viewModel.dismissUserMessage()
        }
    }
}