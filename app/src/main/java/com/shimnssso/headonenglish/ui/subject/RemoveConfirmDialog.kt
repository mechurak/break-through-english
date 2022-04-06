package com.shimnssso.headonenglish.ui.subject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.room.DatabaseSubject

@Composable
fun RemoveConfirmDialog(
    subject: DatabaseSubject,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Remove this item?",
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = subject.title,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (!subject.description.isNullOrEmpty()) {
                    Text(text = subject.description, style = MaterialTheme.typography.body2)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(subject.subjectId) }) {
                Text(text = "Remove")
            }
        }
    )
}