package com.shimnssso.headonenglish.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.shimnssso.headonenglish.model.StyleItem
import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.ui.lecture.CardMode
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

    fun getStyleItemPair(cell: Cell, mode: CardMode): Pair<String, List<StyleItem>> {

        val LIGHT_PURPLE = Color(0xFF7e57c2)

        var tempStr = cell.formattedValue!!
        val retList = mutableListOf<StyleItem>()

        val baseColor = when (mode) {
            CardMode.HideText -> Color.LightGray
            else -> Color.DarkGray
        }

        if (mode != CardMode.HideText) {
            cell.textFormatRuns?.let {
                var curItem: StyleItem? = null
                cell.textFormatRuns.forEachIndexed { index, textFormat ->
                    if (curItem != null) {
                        val prevEndIndex = textFormat.startIndex!!
                        curItem!!.end = prevEndIndex
                        retList.add(curItem!!)
                    }

                    var backgroundColor = Color.Unspecified
                    var color = Color.Unspecified

                    if (textFormat.format.underline == true) {
                        backgroundColor = Color.Yellow
                    }
                    if (textFormat.format.italic == true) {
                        color = LIGHT_PURPLE
                    }
                    curItem = StyleItem(SpanStyle(background = backgroundColor, color = color), textFormat.startIndex ?: 0, -1)
                }
                curItem!!.end = cell.formattedValue!!.length
                retList.add(curItem!!)

                // for bold
                var prevStartIndex = -1
                var prevBold = false
                cell.textFormatRuns.forEachIndexed { index, textFormat ->
                    if (textFormat.format.bold == true) {
                        if (prevBold) {
                            // Do nothing
                        } else {
                            prevStartIndex = textFormat.startIndex ?: 0
                            prevBold = true
                        }
                    } else {
                        if (prevStartIndex != -1) {
                            retList.add(StyleItem(SpanStyle(background = baseColor, color = baseColor), prevStartIndex, textFormat.startIndex!!, true))
                        }
                        prevStartIndex = -1
                        prevBold = false
                    }
                }
                if (prevStartIndex != -1) {
                    retList.add(StyleItem(SpanStyle(background = baseColor, color = baseColor), prevStartIndex, cell.formattedValue!!.length, true))
                }
            }

            if (cell.effectiveFormat?.textFormat?.bold == true) {
                retList.add(StyleItem(SpanStyle(background = baseColor, color = baseColor), 0, cell.formattedValue!!.length, true))
            }
        }

        if (mode == CardMode.HideText) {
            var start = 0
            var endSpace = tempStr.indexOfAny(listOf(" ", ",",  ".", "!", "?", "/"), start)
            while (endSpace > 0) {
                if ((endSpace - start) > 2) {
                    retList.add(
                        StyleItem(
                            SpanStyle(background = baseColor, color = baseColor),
                            start,
                            endSpace,
                            true
                        )
                    )
                }
                start = endSpace + 1
                endSpace = tempStr.indexOfAny(listOf(" ", ",",  ".", "!", "?", "/"), start)
            }
            endSpace = tempStr.length
            if ((endSpace - start) > 2) {
                retList.add(
                    StyleItem(
                        SpanStyle(background = baseColor, color = baseColor),
                        start,
                        endSpace,
                        true
                    )
                )
            }
        }
        return Pair(tempStr, retList)
    }
}