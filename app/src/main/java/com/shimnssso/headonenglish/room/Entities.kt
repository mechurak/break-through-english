package com.shimnssso.headonenglish.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shimnssso.headonenglish.model.DomainCard

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

@Entity(tableName = "lecture_table", primaryKeys = ["subjectId", "date"])
data class DatabaseLecture(
    val subjectId: Int,
    val date: String,

    val title: String,
    val category: String? = null,
    val remoteUrl: String? = null,
    val localUrl: String? = null,
    val link1: String? = null,
    val link2: String? = null,
)

@Entity(tableName = "card_table", primaryKeys = ["subjectId", "date", "order"])
data class DatabaseCard(
    val subjectId: Int,
    val date: String,
    val order: Int,

    val text: String?, // json formatted string that represent Cell. ex: {"formattedValue":"seasonal fruits","textFormatRuns":[{"format":{"underline":true}},{"startIndex":3,"format":{}}]}
    val note: String?,
    val memo: String?,
)

@Entity(tableName = "global_table")
data class DatabaseGlobal(
    val subjectId: Int,

    @PrimaryKey
    val id: Int = 0,
)

fun List<DatabaseCard>.asDomainCard(): List<DomainCard> {
    return map {
        DomainCard(
            date = it.date,
            order = it.order,
            text = it.text!!,
            note = it.note,
            memo = it.memo,
        )
    }
}