package com.shimnssso.headonenglish.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.utils.DateConverter

@Composable
fun LectureCard(
    lecture: DatabaseLecture,
    navigateToLecture: (String) -> Unit,
) {
    val backgroundColor = if (DateConverter.weekInYear(lecture.date) % 2 == 0) {
        MaterialTheme.colors.background
    } else {
        MaterialTheme.colors.surface
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .clickable(onClick = { navigateToLecture(lecture.date) })
            .padding(16.dp)
    ) {
        Column {
            val dateStr = DateConverter.withDayName(lecture.date)
            Text(
                text = dateStr,
                style = MaterialTheme.typography.overline,
            )
            Text(lecture.title, style = MaterialTheme.typography.subtitle1)
            Text(
                text = lecture.category,
                style = MaterialTheme.typography.overline,
            )
        }

        if (lecture.url != null) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
            )
        }
    }
}