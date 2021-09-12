package com.shimnssso.headonenglish.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.navigationBarsPadding
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.room.FakeData
import com.shimnssso.headonenglish.ui.components.InsetAwareTopAppBar
import timber.log.Timber

@ExperimentalAnimationApi
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
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

                    if (!showDialog) {
                        Button(onClick = {
                            if (curIdx == cards.size - 1) {
                                showDialog = true
                            } else {
                                viewModel.next()
                            }
                        }) {
                            Text(
                                text = "Next",
                                modifier = Modifier.padding(30.dp)
                            )
                        }
                    }
                } else {
                    Text(text = "no quiz")
                }
            }

            if (showDialog) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_celebration))
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever,
                )

                LottieAnimation(
                    composition,
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.TopCenter),
                    progress = progress,
                )
            }
            AnimatedVisibility(
                visible = showDialog,
                enter = slideInVertically(
                    // Enters by sliding up from offset -fullHeight to 0.
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
                ) + fadeIn(),
                exit = slideOutVertically(
                    // Exits by sliding right from offset 0 to fullHeight.
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
                ) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
            ) {
                Card(
                    backgroundColor = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Practice Again?", modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colors.onPrimary
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    showDialog = false
                                    onBack()
                                },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("No")
                            }
                            Button(
                                onClick = {
                                    viewModel.first()
                                    showDialog = false
                                },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("Yes")
                            }
                        }
                    }
                }
            }
        }
    }
}