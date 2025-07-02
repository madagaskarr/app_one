package io.tigranes.app_one.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoodCheckInDialog(
    onDismiss: () -> Unit,
    onMoodSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "How are you feeling today?",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodEmoji(emoji = "😔", rating = 1, onSelect = onMoodSelected)
                MoodEmoji(emoji = "😟", rating = 2, onSelect = onMoodSelected)
                MoodEmoji(emoji = "😐", rating = 3, onSelect = onMoodSelected)
                MoodEmoji(emoji = "😊", rating = 4, onSelect = onMoodSelected)
                MoodEmoji(emoji = "😄", rating = 5, onSelect = onMoodSelected)
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later")
            }
        }
    )
}

@Composable
private fun MoodEmoji(
    emoji: String,
    rating: Int,
    onSelect: (Int) -> Unit
) {
    Text(
        text = emoji,
        fontSize = 36.sp,
        modifier = Modifier
            .clickable { onSelect(rating) }
            .padding(8.dp)
    )
}