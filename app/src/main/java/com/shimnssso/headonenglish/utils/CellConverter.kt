package com.shimnssso.headonenglish.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.shimnssso.headonenglish.model.StyleItem
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

    fun getStyleItemPair(cell: Cell): Pair<String, List<StyleItem>> {
        var tempStr = cell.formattedValue!!
        val retList = mutableListOf<StyleItem>()
        cell.textFormatRuns?.let {
            var curItem: StyleItem? = null
            cell.textFormatRuns.forEachIndexed { index, textFormat ->
                when (index) {
                    0 -> {
                        val backgroundColor =
                            if (textFormat.format.underline == true) Color.Yellow else Color.Unspecified
                        curItem = StyleItem(SpanStyle(background = backgroundColor), 0, -1)
                    }
                    else -> {
                        val prevEndIndex = textFormat.startIndex!!
                        curItem!!.end = prevEndIndex
                        retList.add(curItem!!)

                        val backgroundColor =
                            if (textFormat.format.underline == true) Color.Yellow else Color.Unspecified
                        curItem = StyleItem(SpanStyle(background = backgroundColor), textFormat.startIndex, -1)
                    }
                }
            }
            curItem!!.end = cell.formattedValue!!.length
            retList.add(curItem!!)
        }

        var end = tempStr.lastIndexOf(")")
        while (end > 0) {
            val start = tempStr.lastIndexOf("(")
            if (start < 0) {
                throw Exception("mis-matched parentheses")
            }
            retList.forEach { item ->
                item.start = when {
                    item.start > end -> item.start - 2
                    item.start in (start + 1) until end -> item.start - 1
                    else -> item.start
                }
                item.end = when {
                    item.end > end -> item.end - 2
                    item.end in (start + 1)..end -> item.end - 1
                    else -> item.end
                }
            }
            tempStr = tempStr.replaceRange(end, end + 1, "")
            tempStr = tempStr.replaceRange(start, start + 1, "")
            retList.add(StyleItem(SpanStyle(background = Color.DarkGray, color = Color.DarkGray), start, end - 1, true))  // for annotation

            end = tempStr.lastIndexOf(')')
        }
        return Pair(tempStr, retList)
    }
}