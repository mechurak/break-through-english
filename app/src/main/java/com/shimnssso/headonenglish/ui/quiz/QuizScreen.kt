package com.shimnssso.headonenglish.ui.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
            modifier = Modifier.padding(innerPadding)
        ) {
            if (cards.isNotEmpty()) {
                Text(
                    text = "${curIdx + 1} / ${cards.size}",
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                QuizContent(
                    card = cards[curIdx],
                    success = {
                        Timber.i("success")
                    },
                    fail = {
                        Timber.i("fail")
                    }
                )
                Button(onClick = { viewModel.next() }) {
                    Text(text = "Next")
                }
            } else {
                Text(text = "no quiz")
            }
        }
    }
}