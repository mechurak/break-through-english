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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.utils.DateConverter

@Composable
fun LectureCard(
    lecture: DatabaseLecture,
    navigateToArticle: (Int, String) -> Unit = { _, _ -> },
    navigateToQuiz: (Int, String) -> Unit = { _, _ -> },
    isRecent: Boolean = false,
) {
    val backgroundColor = MaterialTheme.colors.surface
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
            Text(
                text = lecture.date,
                style = MaterialTheme.typography.overline,
                color = if (isRecent) Color.Blue else Color.Unspecified,
                fontWeight = if (isRecent) FontWeight.ExtraBold else FontWeight.Normal,
            )
            Text(lecture.title, style = MaterialTheme.typography.subtitle1)
            Text(
                text = lecture.category ?: "",
                style = MaterialTheme.typography.overline,
            )

            val lastStudyDateStr = if (lecture.lastStudyDate == 0L) "" else {
                "(last study: ${DateConverter.getDateStrFromLong(lecture.lastStudyDate)})"
            }

            Text(
                text = lastStudyDateStr,
                style = MaterialTheme.typography.caption,
                fontSize = 8.sp,
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

@Preview("LectureCardPreview")
@Composable
fun LectureCardPreview() {
    val lecture = DatabaseLecture(
        subjectId = 1,
        date = "2021-05-12",
        title = "발음 강세 Unit 553. 체중",
        category = "Maintaining Our Health",
        remoteUrl = "tempUrl",
        localUrl = null,
        link1 = null,
        link2 = null,
        lastStudyDate = 1649297470436L,  // 2022-04-07
        studyPoint = 0,
        quizCount = 1,
    )
    LectureCard(lecture = lecture)
}