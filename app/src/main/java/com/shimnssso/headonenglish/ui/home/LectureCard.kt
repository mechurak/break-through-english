package com.shimnssso.headonenglish.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.room.DatabaseLecture

@Composable
fun LectureCard(
    lecture: DatabaseLecture,
    navigateToLecture: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { navigateToLecture(lecture.date) })
            .padding(16.dp)
    ) {
        Column() {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = lecture.date,
                    style = MaterialTheme.typography.overline
                )
            }
            Text(lecture.title, style = MaterialTheme.typography.subtitle1)
        }

        if (lecture.url != null) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
            )
        }
    }
}