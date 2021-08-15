package com.shimnssso.headonenglish.googlesheet

import com.google.api.services.sheets.v4.model.RowData
import timber.log.Timber

data class IndexHolder(
    var date: Int = 0, // both
    var order: Int = 1,  // both
    var text: Int = 2,
    var hint: Int = -1,  // optional
    var note: Int = -1,  // optional
    var memo: Int = -1,  // optional
    var quiz: Int = -1,  // optional

    var metaTitle: Int = 2,
    var metaCategory: Int = -1,  // optional
    var metaRemoteUrl: Int = -1,  // optional
    var metaLink1: Int = -1,  // optional
    var metaLink2: Int = -1,  // optional
) {
    fun setColumnIndices(rowData: RowData) {
        val cells = rowData.getValues()
        for ((i, cell) in cells.withIndex()) {
            when (cell.formattedValue) {
                "date", "day", "lecture" -> {
                    date = i
                }
                "order" -> {
                    order = i
                }
                "text" -> {
                    text = i
                }
                "hint" -> {
                    hint = i
                }
                "note" -> {
                    note = i
                }
                "memo" -> {
                    memo = i
                }
                "quiz" -> {
                    quiz = i
                }
                "title" -> {
                    metaTitle = i
                }
                "category" -> {
                    metaCategory = i
                }
                "remoteUrl" -> {
                    metaRemoteUrl = i
                }
                "link1" -> {
                    metaLink1 = i
                }
                "link2" -> {
                    metaLink2 = i
                }
                else -> {
                    Timber.w("unknown keyword (${cell.formattedValue}). skip it. cell[$i]")
                }
            }
        }
    }
}