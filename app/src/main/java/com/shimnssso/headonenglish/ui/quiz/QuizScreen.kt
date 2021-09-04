package com.shimnssso.headonenglish.ui.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shimnssso.headonenglish.room.FakeData
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar
import timber.log.Timber

@Composable
fun QuizScreen(
    subject: String?,
    date: String?,
    onBack: () -> Unit
) {
    val subjectId = subject!!.toInt()
    val viewModel =
        viewModel(QuizViewModel::class.java, factory = QuizViewModel.Factory(subjectId, date!!))
    val cards by viewModel.cards.observeAsState(listOf())
    val lecture by viewModel.lecture.observeAsState(FakeData.DEFAULT_LECTURE)
    val curIdx by viewModel.curIdx.observeAsState(0)
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = lecture.title,
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            if (cards.isNotEmpty()) {
                Text(
                    text = "${curIdx + 1} / ${cards.size}",
                    style = MaterialTheme.typography.overline,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                QuizContent(
                    idx = curIdx,
                    card = cards[curIdx],
                    success = { quizIdx ->
                        Timber.i("success")
                        if (quizIdx == cards.size - 1) {
                            showDialog = true
                        }
                    },
                    fail = {
                        Timber.i("fail")
                        viewModel.next()
                    }
                )
                Button(onClick = { viewModel.next() }) {
                    Text(
                        text = "Next",
                        modifier = Modifier.padding(30.dp)
                    )
                }
            } else {
                Text(text = "no quiz")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.first()
                        showDialog = false
                    },
                    title = {
                        Text("Practice Again?")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.first()
                                showDialog = false
                            }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                onBack()
                            }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}