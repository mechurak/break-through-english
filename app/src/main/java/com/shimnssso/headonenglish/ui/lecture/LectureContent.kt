package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    ) {
        LectureRealContent(
            lecture = lecture,
            cards = cards,
            modifier = modifier,
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
    Surface(elevation = 8.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .navigationBarsPadding()
                .height(56.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "temp thumb up"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "temp settings"
                )
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
