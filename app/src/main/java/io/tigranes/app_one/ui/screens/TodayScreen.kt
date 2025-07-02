package io.tigranes.app_one.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.tigranes.app_one.ui.components.*
import io.tigranes.app_one.ui.viewmodels.MoodViewModel
import io.tigranes.app_one.ui.viewmodels.TaskViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TodayScreen(
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel = hiltViewModel(),
    moodViewModel: MoodViewModel = hiltViewModel()
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    
    val tasks by taskViewModel.tasks.collectAsState()
    val uiState by taskViewModel.uiState.collectAsState()
    val hasMoodToday by moodViewModel.hasMoodToday.collectAsState()
    
    var showMoodDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(today) {
        taskViewModel.selectDate(today)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Leave space for FAB
        ) {
            // Show mood check-in card if user hasn't checked in today
            if (!hasMoodToday) {
                MoodCheckInCard(
                    onCheckIn = { showMoodDialog = true },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            TaskList(
                tasks = tasks,
                onToggleComplete = { taskId ->
                    taskViewModel.toggleTaskCompletion(taskId)
                },
                onMoveToTomorrow = { taskId ->
                    taskViewModel.moveTaskToTomorrow(taskId)
                },
                onDelete = { task ->
                    taskViewModel.deleteTask(task)
                },
                emptyMessage = "No tasks for today.\nTap + to add one!",
                modifier = Modifier.weight(1f)
            )
        }
        
        AddTaskFab(
            onAddTask = { title, category, isForTomorrow ->
                taskViewModel.addTask(title, category, isForTomorrow)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            defaultForTomorrow = false
        )
    }
    
    if (showMoodDialog) {
        MoodCheckInDialog(
            onDismiss = { showMoodDialog = false },
            onMoodSelected = { mood ->
                scope.launch {
                    moodViewModel.recordMood(mood)
                }
            }
        )
    }
    
    uiState.userMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar or toast here if needed
            taskViewModel.dismissUserMessage()
        }
    }
}