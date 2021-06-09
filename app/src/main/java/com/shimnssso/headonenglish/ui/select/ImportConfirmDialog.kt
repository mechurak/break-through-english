package com.shimnssso.headonenglish.ui.select

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.api.services.drive.model.File

@Composable
fun ImportConfirmDialog(
    files: List<File>,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIdx by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Import the selected sheet?",
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                files.forEachIndexed { index, file ->
                    val modifier = if (selectedIdx == index) Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            MaterialTheme.colors.primaryVariant,
                        ) else Modifier.fillMaxWidth()
                    TextButton(
                        onClick = { selectedIdx = index },
                        modifier = modifier
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                file.name,
                                style = MaterialTheme.typography.body1
                            )
                            Text(
                                "   by ${file.owners[0].emailAddress}",
                                style = MaterialTheme.typography.caption,
                            )
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(files[selectedIdx].name, files[selectedIdx].id) }) {
                Text(text = "Confirm")
            }
        }
    )
}