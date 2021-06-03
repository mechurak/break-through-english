package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.network.Format
import com.shimnssso.headonenglish.network.TextFormat
import com.shimnssso.headonenglish.utils.CellConverter
import timber.log.Timber

@Composable
fun FormattedText(
    cell: Cell,
    mode: CardMode,
    modifier: Modifier = Modifier,
    showKeyword: Boolean = false,
    toggleDescription: () -> Unit = {},
) {
    val (text, itemList) = CellConverter.getStyleItemPair(cell, mode)

    val showStartList = remember { mutableStateListOf<Int>() }

    val annotatedText = buildAnnotatedString {
        append(text)
        itemList.forEach {
            if (it.isAnnotation) {
                if (it.start in showStartList) {
                    val tempStyle = when (mode) {
                        CardMode.HideText -> it.spanStyle!!.copy(
                            background = Color.Unspecified,
                            color = Color.Unspecified
                        )  // for all
                        else -> it.spanStyle!!.copy(background = Color.Unspecified, color = Color.Red)  // 1, 3
                    }
                    addStyle(tempStyle, it.start, it.end)
                } else {
                    addStyle(it.spanStyle!!, it.start, it.end)
                }
                addStringAnnotation("Section", "important", it.start, it.end)
            } else {
                addStyle(it.spanStyle!!, it.start, it.end)
            }
        }
    }

    LaunchedEffect(mode, showKeyword) {
        showStartList.clear()
        if (showKeyword) {
            val sections = annotatedText.getStringAnnotations("Section", 0, annotatedText.length)
            sections.forEach {
                showStartList.add(it.start)
            }
        }
    }

    val textStyle = when (mode) {
        CardMode.HideText -> MaterialTheme.typography.h5.copy(fontSize = 30.sp)
        else -> MaterialTheme.typography.body1.copy(fontSize = 20.sp)
    }

    ClickableText(
        text = annotatedText,
        style = textStyle,
        onClick = { offset ->
            val selectedSection = annotatedText.getStringAnnotations("Section", offset, offset).firstOrNull()
            if (selectedSection != null) {
                Timber.d(
                    "offset: $offset, text: ${
                        annotatedText.text.substring(
                            selectedSection.start,
                            selectedSection.end
                        )
                    }"
                )
                if (selectedSection.start in showStartList) {
                    showStartList.remove(selectedSection.start)
                } else {
                    showStartList.add(selectedSection.start)
                }
                Timber.d("showStartList: $showStartList")
            }
            toggleDescription()  // Propagate click event
        },
        modifier = modifier
    )
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
    FormattedText(cell, mode = CardMode.Default)
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

    // Partially Clickable Text (https://stackoverflow.com/a/65656351)
    val annotatedText = buildAnnotatedString {
        append("invest in (stocks and equities)")
        addStyle(SpanStyle(background = Color.Yellow), 2, 6)
        addStyle(SpanStyle(background = Color.Yellow), 11, 17)
        addStyle(SpanStyle(background = Color.Yellow), 22, 24)
        addStyle(SpanStyle(background = Color.DarkGray, color = Color.DarkGray), 21, 31)
        addStringAnnotation("Section", "important", 21, 31)
    }
    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations("Section", offset, offset).firstOrNull()?.let {
                Timber.d("offset: $offset, text: ${annotatedText.text.substring(it.start, it.end)}")
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}