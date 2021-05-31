package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
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
import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.network.Format
import com.shimnssso.headonenglish.network.TextFormat
import com.shimnssso.headonenglish.utils.CellConverter
import timber.log.Timber

@Composable
fun FormattedText(
    cell: Cell,
    modifier: Modifier = Modifier,
    defaultShowKeyword: Boolean = false,
    toggleDescription: () -> Unit = {},
) {
    val (text, itemList) = CellConverter.getStyleItemPair(cell)

    val showStartList = remember { mutableStateListOf<Int>() }

    val annotatedText = buildAnnotatedString {
        append(text)
        itemList.forEach {
            if (it.isAnnotation) {
                if (it.start in showStartList) {
                    val tempStyle = it.spanStyle!!.copy(background = Color.Unspecified, color = Color.Red)
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

    LaunchedEffect(defaultShowKeyword) {
        showStartList.clear()
        if (defaultShowKeyword) {
            val sections = annotatedText.getStringAnnotations("Section", 0, annotatedText.length)
            sections.forEach {
                showStartList.add(it.start)
            }
        }
    }

    ClickableText(
        text = annotatedText,
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
            } else {
                toggleDescription()  // Propagate click event
            }
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