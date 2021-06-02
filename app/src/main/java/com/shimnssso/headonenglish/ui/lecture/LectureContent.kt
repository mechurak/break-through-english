package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
            cards.forEach { card ->
                RowCard(
                    card, defaultMode, defaultShowKeyword
                )
            }
            Spacer(Modifier.height(100.dp))
        }
    }
}