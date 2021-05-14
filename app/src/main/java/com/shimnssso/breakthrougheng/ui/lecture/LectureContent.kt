package com.shimnssso.breakthrougheng.ui.lecture

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimnssso.breakthrougheng.model.Lecture

private val defaultSpacerSize = 16.dp

@Composable
fun LectureContent(post: Lecture, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(horizontal = defaultSpacerSize)
    ) {
        item {
            Text(text = post.title, style = MaterialTheme.typography.h4)
            Spacer(Modifier.height(8.dp))
        }
        items(post.rows) { row ->
            Text(text = row.spelling, style = MaterialTheme.typography.h5)
            Text(text = row.meaning, style = MaterialTheme.typography.h5)
        }
        item {
            Spacer(Modifier.height(48.dp))
        }
    }
}