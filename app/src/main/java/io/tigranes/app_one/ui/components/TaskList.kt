package io.tigranes.app_one.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.tigranes.app_one.data.model.Task

@Composable
fun TaskList(
    tasks: List<Task>,
    onToggleComplete: (Long) -> Unit,
    onMoveToTomorrow: (Long) -> Unit,
    onDelete: (Task) -> Unit,
    emptyMessage: String = "No tasks for this day",
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        EmptyState(
            message = emptyMessage,
            modifier = modifier
        )
    } else {
        var selectedTaskId by remember { mutableStateOf<Long?>(null) }
        
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = tasks,
                key = { it.id }
            ) { task ->
                TaskCardWithMenu(
                    task = task,
                    isSelected = selectedTaskId == task.id,
                    onToggleComplete = { onToggleComplete(task.id) },
                    onSelect = { selectedTaskId = task.id },
                    onDismissMenu = { selectedTaskId = null },
                    onMoveToTomorrow = { 
                        onMoveToTomorrow(task.id)
                        selectedTaskId = null
                    },
                    onDelete = { 
                        onDelete(task)
                        selectedTaskId = null
                    }
                )
            }
        }
    }
}

@Composable
private fun TaskCardWithMenu(
    task: Task,
    isSelected: Boolean,
    onToggleComplete: () -> Unit,
    onSelect: () -> Unit,
    onDismissMenu: () -> Unit,
    onMoveToTomorrow: () -> Unit,
    onDelete: () -> Unit
) {
    Box {
        TaskCard(
            task = task,
            onToggleComplete = onToggleComplete,
            onLongClick = onSelect
        )
        
        DropdownMenu(
            expanded = isSelected,
            onDismissRequest = onDismissMenu
        ) {
            if (!task.completed) {
                DropdownMenuItem(
                    text = { Text("Move to Tomorrow") },
                    onClick = onMoveToTomorrow
                )
            }
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = onDelete
            )
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "✨",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}