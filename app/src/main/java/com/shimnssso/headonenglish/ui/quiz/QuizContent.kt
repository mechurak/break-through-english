package com.shimnssso.headonenglish.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.ui.components.AnswerTextField
import com.shimnssso.headonenglish.ui.components.MultiLineRow
import com.shimnssso.headonenglish.utils.CellConverter
import timber.log.Timber

@Composable
fun QuizContent(
    card: DomainCard,
    success: () -> Unit,
    fail: () -> Unit,
) {
    val spellingCell: Cell = CellConverter.fromJson(card.text)
    val hasHint = !card.hint.isNullOrEmpty()
    val hasNote = !card.note.isNullOrEmpty()
    val hasMemo = !card.memo.isNullOrEmpty()



    Column() {

        if (hasHint) {
            Text(
                text = card.hint!!,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            Text(
                text = "no description"
            )
        }

        Spacer(Modifier.height(50.dp))

        val quizAnswerPair = CellConverter.getQuizAnswerPair(spellingCell)
        Timber.e("quizAnswerPair: $quizAnswerPair")

        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            MultiLineRow {
                quizAnswerPair.map {
                    if (it.first) {
                        AnswerTextField(
                            expectedText = it.second,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    } else {
                        Text(text = it.second, modifier = Modifier.padding(end = 6.dp))
                    }
                }
            }
        }


        Spacer(Modifier.height(50.dp))

        if (hasNote) {
            Text(
                text = card.note!!,
                style = MaterialTheme.typography.overline,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        if (hasMemo) {
            Text(
                text = card.memo!!,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}