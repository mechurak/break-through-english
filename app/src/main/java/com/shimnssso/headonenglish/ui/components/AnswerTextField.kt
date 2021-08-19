package com.shimnssso.headonenglish.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AnswerTextField(
    expectedText: String,
) {
    var value by remember { mutableStateOf("") }
    val maxChar = expectedText.length
    val width = maxChar * 20
    val isError = value != "" && value != expectedText

    OutlinedTextField(
        value = value,
        onValueChange = { value = it },
        isError = isError,
        singleLine = true,
        placeholder = {
            Text(text = "o".repeat(maxChar))
        },
        modifier = Modifier.width(width.dp)
    )
}

@Preview
@Composable
fun AnswerTextFieldPreview() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Soon, our plane took ")
        AnswerTextField(expectedText = "off")
        Text(".")
    }
}