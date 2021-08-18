package com.shimnssso.headonenglish.ui.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.ui.lecture.CardMode
import com.shimnssso.headonenglish.ui.lecture.FormattedText
import com.shimnssso.headonenglish.utils.CellConverter

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
                style = MaterialTheme.typography.caption,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        FormattedText(
            cell = spellingCell,
            mode = CardMode.Default,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
            showKeyword = false
        )

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