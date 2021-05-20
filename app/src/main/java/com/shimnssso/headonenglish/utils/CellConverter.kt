package com.shimnssso.headonenglish.utils

import com.shimnssso.headonenglish.model.FormattedToken
import com.shimnssso.headonenglish.network.Cell
import com.squareup.moshi.Moshi

object CellConverter {
    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter(Cell::class.java)

    fun toJsonStr(cell: Cell): String {
        return adapter.toJson(cell)
    }

    fun fromJson(jsonStr: String): Cell {
        return adapter.fromJson(jsonStr)!!
    }

    fun toSourceList(cell: Cell): List<FormattedToken> {
        val retList = mutableListOf<FormattedToken>()
        if (cell.textFormatRuns == null) {
            retList.add(FormattedToken(false, cell.formattedValue))
            return retList
        }
        var shouldAccent = false
        var curIndex = 0
        cell.textFormatRuns.forEachIndexed { index, textFormat ->
            when (index) {
                0 -> {
                    shouldAccent = textFormat.format.underline == true
                    curIndex = 0
                }
                else -> {
                    val prevEndIndex = textFormat.startIndex!!
                    retList.add(
                        FormattedToken(
                            shouldAccent,
                            cell.formattedValue.substring(curIndex, prevEndIndex)
                        )
                    )
                    curIndex = textFormat.startIndex
                    shouldAccent = textFormat.format.underline == true
                }
            }
        }
        retList.add(FormattedToken(shouldAccent, cell.formattedValue.substring(curIndex)))
        return retList
    }
}