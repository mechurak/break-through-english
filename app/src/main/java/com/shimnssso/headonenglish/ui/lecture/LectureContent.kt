package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.ui.MainActivity

@Composable
fun LectureContent(
    lecture: DatabaseLecture,
    cards: List<DomainCard>,
    modifier: Modifier = Modifier,
    defaultMode: CardMode,
    defaultShowKeyword: Boolean,
    showBackdrop: Boolean,
    changeShowBackdrop: () -> Unit,
) {
    val activity = LocalContext.current as MainActivity

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (lecture.remoteUrl == null && lecture.localUrl == null) {
            Button(
                onClick = { activity.launchAudioChooser(lecture) },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Set an audio file")
            }
        } else if (lecture.localUrl != null) {
            ExoPlayerView(url = lecture.localUrl)
        } else if (lecture.remoteUrl != null) {
            ExoPlayerView(url = lecture.remoteUrl)
        }

        var focusedIdx by remember { mutableStateOf(-1) }
        val lazyListState = rememberLazyListState()

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.walking_broccoli))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever,
        )

        Box() {
            LazyColumn(
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(cards) { index, card ->
                    RowCard(
                        index, card, defaultMode, defaultShowKeyword, index == focusedIdx
                    ) { newFocusedIdx ->
                        focusedIdx = newFocusedIdx
                    }
                }

                item {
                    Spacer(Modifier.height(200.dp))
                }

                item {
                    LottieAnimation(
                        composition,
                        modifier = Modifier
                            .size(250.dp),
                        progress = progress,
                    )
                }

                item {
                    Spacer(Modifier.height(200.dp))
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                showBackdrop,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            changeShowBackdrop()
                        },
                    color = Color.Black.copy(0.7f)
                ) {
                }
            }
        }
    }
}