package com.shimnssso.headonenglish.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AnswerTextField(
    modifier: Modifier = Modifier,
    idx: Int = 0,
    expectedText: String,
    value: String = "",
    onValueChanged: (Int, String) -> Unit = { _, _ -> },
    focusRequester: FocusRequester = FocusRequester(),
    onNext: (Int) -> Unit = {}
) {
    val maxChar = expectedText.length
    val width = (maxChar * 8) + 40
    val isError = value != expectedText

    LaunchedEffect(expectedText) {
        if (idx == 0) {
            focusRequester.requestFocus()
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChanged(idx, it) },
        isError = isError,
        singleLine = true,
        placeholder = {
            Text(text = "_".repeat(maxChar))
        },
        modifier = modifier
            .width(width.dp)
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNext(idx) })
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