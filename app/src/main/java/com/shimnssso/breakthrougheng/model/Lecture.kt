package com.shimnssso.breakthrougheng.model

import androidx.annotation.DrawableRes

data class Lecture(
    val date: String,
    val title: String,
    val url: String,
    val rows: List<Row>,
)

data class Row(
    val id: Int,
    val spelling: String,
    val meaning: String,
    val description: String
)