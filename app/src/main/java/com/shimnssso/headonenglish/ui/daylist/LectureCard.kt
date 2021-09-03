package com.shimnssso.headonenglish.ui.daylist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.utils.DateConverter

@Composable
fun LectureCard(
    lecture: DatabaseLecture,
    navigateToArticle: (Int, String) -> Unit,
    navigateToQuiz: (Int, String) -> Unit,
) {
    val isDateBase = DateConverter.isDateBase(lecture.date)
    val backgroundColor = if (isDateBase) {
        if (DateConverter.weekInYear(lecture.date) % 2 == 0) {
            MaterialTheme.colors.background
        } else {
            MaterialTheme.colors.surface
        }
    } else {
        MaterialTheme.colors.surface
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .clickable(onClick = { navigateToArticle(lecture.subjectId, lecture.date) })
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(0.95f)
        ) {
            val dateStr = if (isDateBase) {
                DateConverter.withDayName(lecture.date)
            } else {
                lecture.date
            }
            Text(
                text = dateStr,
                style = MaterialTheme.typography.overline,
            )
            Text(lecture.title, style = MaterialTheme.typography.subtitle1)
            Text(
                text = lecture.category ?: "",
                style = MaterialTheme.typography.overline,
            )
        }

        if (lecture.remoteUrl != null) {
            Image(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = "play icon",
                modifier = Modifier
                    .padding(4.dp)
                    .size(48.dp)
                    .padding(8.dp)
            )
        }

        if (lecture.quizCount > 0) {
            Button(onClick = { navigateToQuiz(lecture.subjectId, lecture.date) }) {
                Text(text = "Quiz")
            }
        }
    }
}