package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.network.Format
import com.shimnssso.headonenglish.network.TextFormat
import com.shimnssso.headonenglish.utils.CellConverter

@Composable
fun FormattedText(cell: Cell) {
    if (cell.textFormatRuns.isNullOrEmpty()) {
        Text(text = cell.formattedValue ?: "", style = MaterialTheme.typography.body1)
    } else {
        val formattedStringSourceList = CellConverter.toSourceList(cell)
        val annotatedStr = buildAnnotatedString {
            formattedStringSourceList.forEach {
                if (it.accent) {
                    withStyle(style = SpanStyle(background = Color.Yellow)) {
                        append(it.str)
                    }
                } else {
                    append(it.str)
                }
            }
        }
        Text(text = annotatedStr, style = MaterialTheme.typography.body1)
    }
}

@Preview("FormattedText", widthDp = 360, heightDp = 120, showBackground = true)
@Composable
fun FormattedTextPreview() {
    val cell = Cell(
        formattedValue = "seasonal fruits",
        textFormatRuns = listOf(
            TextFormat(startIndex = null, format = Format(underline = true, bold = null)),
            TextFormat(startIndex = 3, format = Format(underline = null, bold = null))
        )
    )
    FormattedText(cell)
}

@Preview("FormattedText", widthDp = 360, heightDp = 120, showBackground = true)
@Composable
fun TargetFormattedTextPreview() {
    Text(buildAnnotatedString {
        append("in")
        withStyle(style = SpanStyle(background = Color.Yellow)) {
            append("vest")
        }
        append(" in ")
        withStyle(style = SpanStyle(background = Color.Yellow)) {
            append("stocks")
        }
        append(" and ")
        withStyle(style = SpanStyle(background = Color.Yellow)) {
            append("eq")
        }
        append("uities")
    })
}

@Preview("FormattedText2", widthDp = 360, heightDp = 120, showBackground = true)
@Composable
fun TargetFormattedTextPreview2() {
    Text(buildAnnotatedString {
        append("invest in stock and equities")
        addStyle(SpanStyle(background = Color.Yellow), 2, 6)
        addStyle(SpanStyle(background = Color.Yellow), 9, 15)
        addStyle(SpanStyle(background = Color.Yellow), 20, 22)
    })
}