package com.shimnssso.headonenglish.model

import androidx.compose.ui.text.SpanStyle

data class StyleItem(
    val spanStyle: SpanStyle? = null,
    var start: Int = -1,
    var end: Int = -1,
    var isAnnotation: Boolean = false
)