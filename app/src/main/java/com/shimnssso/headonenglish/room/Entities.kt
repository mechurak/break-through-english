package com.shimnssso.headonenglish.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject_table")
data class DatabaseSubject(
    @PrimaryKey
    val subjectId: Int,

    val title: String,
    val lastUpdateTime: Long,
    val link: String,
    val sheetId: String,
    val isVideoBased: Boolean,
)


@Entity(tableName = "lecture_table")
data class DatabaseLecture(
    @PrimaryKey
    val date: String,

    val category: String,
    val title: String,
    val remoteUrl: String?,
    val localUrl: String?,

    val subjectId: Int,
)

@Entity(tableName = "card_table", primaryKeys = ["date", "id", "subjectId"])
data class DatabaseCard(
    val date: String,
    val id: Int,
    val spelling: String?, // json formatted string that represent Cell. ex: {"formattedValue":"seasonal fruits","textFormatRuns":[{"format":{"underline":true}},{"startIndex":3,"format":{}}]}
    val meaning: String?,
    val description: String?,

    val subjectId: Int,
)

@Entity(tableName = "global_table")
data class DatabaseGlobal(
    val subjectId: Int,

    @PrimaryKey
    val id: Int = 0,
)