package com.shimnssso.headonenglish.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shimnssso.headonenglish.model.DomainCard

@Entity(tableName = "subject_table")
data class DatabaseSubject(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Int,

    val title: String,
    val sheetId: String,
    val lastUpdateTime: Long,
    val description: String? = null,
    val link: String? = null,
    val subjectForUrl: String? = null,
    val image: String? = null,
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
    @ColumnInfo(defaultValue = "0")
    val lastStudyDate: Long = 0,
    @ColumnInfo(defaultValue = "0")
    val studyPoint: Int = 0,
    @ColumnInfo(defaultValue = "0")
    var quizCount: Int = 0,
)

@Entity(tableName = "card_table", primaryKeys = ["subjectId", "date", "order"])
data class DatabaseCard(
    val subjectId: Int,
    val date: String,
    val order: Int,

    val text: String, // json formatted string that represent Cell. ex: {"formattedValue":"seasonal fruits","textFormatRuns":[{"format":{"underline":true}},{"startIndex":3,"format":{}}]}
    val hint: String?,
    val note: String?,
    val memo: String?,
    @ColumnInfo(defaultValue = "0")
    val isForQuiz: Int = 0,
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
            text = it.text,
            hint = it.hint,
            note = it.note,
            memo = it.memo,
        )
    }
}