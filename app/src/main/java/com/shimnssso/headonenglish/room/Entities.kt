package com.shimnssso.headonenglish.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecture_table")
data class DatabaseLecture(
    @PrimaryKey
    val date: String,

    val category: String,
    val title: String,
    val url: String?,
)

@Entity(tableName = "card_table", primaryKeys = ["date", "id"])
data class DatabaseCard(
    val date: String,
    val id: Int,
    val spelling: String?,
    val meaning: String?,
    val description: String?,
)

@Entity(tableName = "global_table")
data class GlobalInfo(
    @PrimaryKey
    val id: Int = 0,
    val lastUpdateTime: Long,
)