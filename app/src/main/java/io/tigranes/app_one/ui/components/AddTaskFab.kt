package io.tigranes.app_one.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.tigranes.app_one.data.model.Category

@Composable
fun AddTaskFab(
    onAddTask: (title: String, category: Category, isForTomorrow: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    defaultForTomorrow: Boolean = false
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    
    FloatingActionButton(
        onClick = { showBottomSheet = true },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Task"
        )
    }
    
    if (showBottomSheet) {
        AddTaskBottomSheet(
            onDismiss = { showBottomSheet = false },
            onAddTask = onAddTask,
            defaultForTomorrow = defaultForTomorrow
        )
    }
}