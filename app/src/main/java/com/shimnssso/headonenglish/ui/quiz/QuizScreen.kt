package com.shimnssso.headonenglish.ui.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar

@Composable
fun QuizScreen(
    subject: String?,
    date: String?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = "subject: $subject, date: $date",
                        color = LocalContentColor.current
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "temp up"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column() {
            Text(text = "QuizScreen")
            Text(text = "subject: $subject")
            Text(text = "date: $date")
            Button(onClick = { onBack() }) {
                Text(text = "Back")
            }
        }
    }
}