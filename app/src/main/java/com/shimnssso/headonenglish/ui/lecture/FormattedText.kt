package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview


@Preview("Light Theme", widthDp = 360, heightDp = 120, showBackground = true)
@Composable
fun FormattedTextPreview() {
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