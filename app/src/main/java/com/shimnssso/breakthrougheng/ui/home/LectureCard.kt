package com.shimnssso.breakthrougheng.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimnssso.breakthrougheng.model.Lecture

@Composable
fun LectureCard(
    lecture: Lecture,
    navigateToLecture: (String) -> Unit,
) {
    Row(
        Modifier
            .clickable(onClick = { navigateToLecture(lecture.date) })
            .padding(16.dp)
    ) {
        Column(Modifier.weight(1f)) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "BASED ON YOUR HISTORY",
                    style = MaterialTheme.typography.overline
                )
            }
            Text(lecture.title, style = MaterialTheme.typography.subtitle1)
        }
    }
}