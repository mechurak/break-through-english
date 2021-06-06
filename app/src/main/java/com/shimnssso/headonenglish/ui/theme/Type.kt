package com.shimnssso.headonenglish.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        color = Color.Gray,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    caption = TextStyle(
        color = Color.Gray,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        textIndent = TextIndent(0.sp, 12.sp),
        fontSize = 10.sp,
        letterSpacing = 0.4.sp
    ),
    overline = TextStyle(
        color = Color.DarkGray,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        textIndent = TextIndent(0.sp, 12.sp),
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)