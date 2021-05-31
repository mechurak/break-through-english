package com.shimnssso.headonenglish.ui.lecture

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.navigationBarsPadding
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.ui.MainActivity

@Composable
fun LectureContent(
    lecture: DatabaseLecture,
    cards: List<DomainCard>,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    onUpdateCard: (card: DomainCard) -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { AudioBottomBar() }
    ) { innerPadding ->
        LectureRealContent(
            lecture = lecture,
            cards = cards,
            modifier = modifier.padding(innerPadding),
            onUpdateCard = { card -> onUpdateCard(card) },
        )
    }
}

@Composable
fun LectureRealContent(
    lecture: DatabaseLecture,
    cards: List<DomainCard>,
    modifier: Modifier = Modifier,
    onUpdateCard: (card: DomainCard) -> Unit,
) {
    val activity = LocalContext.current as MainActivity

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (lecture.remoteUrl == null && lecture.localUrl == null) {
            Button(
                onClick = { activity.launchAudioChooser(lecture) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Set Media file")
            }
        } else if (lecture.localUrl != null) {
            ExoPlayerView(url = lecture.localUrl)
        } else if (lecture.remoteUrl != null) {
            ExoPlayerView(url = lecture.remoteUrl)
        }

        Column(
            modifier = modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            cards.forEach { card ->
                RowCard(card) { the_card ->
                    onUpdateCard(the_card)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AudioBottomBar(
) {
    val app = LocalContext.current.applicationContext as Application
    val activity = LocalContext.current as MainActivity
    val viewModel = ViewModelProvider(activity, MediaViewModel.Factory(app)).get(MediaViewModel::class.java)

    val showSetting = remember { mutableStateOf(false) }
    val speed by viewModel.speed.observeAsState(initial = 1.0f)

    Surface(elevation = 8.dp) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                // .height(56.dp)
                .fillMaxWidth()
        ) {
            if (showSetting.value) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSetting.value = !showSetting.value }
                ) {
                    Text(
                        "play speed",
                        modifier = Modifier.padding(16.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        IconButton(onClick = { viewModel.speedDown() }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "temp thumb up"
                            )
                        }
                        Text(text = "${speed}x")
                        IconButton(onClick = { viewModel.speedUp() }) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = "temp thumb up"
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "temp thumb up"
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showSetting.value = !showSetting.value }) {
                    Box(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        val iconVector = if (showSetting.value)  Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp
                        Icon(
                            imageVector = iconVector,
                            contentDescription = "temp settings",
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopStart)
                        )
                    }
                    Text(text = "${speed}x")
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "temp settings"
                    )
                }
            }
        }
    }
}