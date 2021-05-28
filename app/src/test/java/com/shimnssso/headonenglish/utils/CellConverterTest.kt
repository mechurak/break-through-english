package com.shimnssso.headonenglish.utils

import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.network.Format
import com.shimnssso.headonenglish.network.TextFormat
import org.junit.Assert
import org.junit.Test

class CellConverterTest {
    // {"formattedValue":"seasonal fruits","textFormatRuns":[{"format":{"underline":true}},{"startIndex":3,"format":{}}]}
    private val CELL_JSON_STR =
        "{\"formattedValue\":\"seasonal fruits\",\"textFormatRuns\":[{\"format\":{\"underline\":true}},{\"startIndex\":3,\"format\":{}}]}"
    private val CELL = Cell(
        formattedValue = "seasonal fruits",
        textFormatRuns = listOf(
            TextFormat(startIndex = null, format = Format(underline = true, bold = null)),
            TextFormat(startIndex = 3, format = Format(underline = null, bold = null))
        )
    )

    private val JSON_STR =
        "{\"formattedValue\":\"Being overweight is (the leading cause of) several chronic illnesses, like heart disease or diabetes.\",\"textFormatRuns\":[{\"format\":{}},{\"startIndex\":25,\"format\":{\"underline\":true}},{\"startIndex\":28,\"format\":{}},{\"startIndex\":43,\"format\":{\"underline\":true}},{\"startIndex\":45,\"format\":{}},{\"startIndex\":51,\"format\":{\"underline\":true}},{\"startIndex\":55,\"format\":{}},{\"startIndex\":59,\"format\":{\"underline\":true}},{\"startIndex\":61,\"format\":{}},{\"startIndex\":83,\"format\":{\"underline\":true}},{\"startIndex\":88,\"format\":{}},{\"startIndex\":95,\"format\":{\"underline\":true}},{\"startIndex\":97,\"format\":{}}]}"

    private val JSON_CELL =
        "{\"formattedValue\":\"The (ROI) could be better if you invest in ETFs.\",\"textFormatRuns\":[{\"format\":{}},{\"startIndex\":5,\"format\":{\"underline\":true}},{\"startIndex\":8,\"format\":{}},{\"startIndex\":19,\"format\":{\"underline\":true}},{\"startIndex\":22,\"format\":{}},{\"startIndex\":35,\"format\":{\"underline\":true}},{\"startIndex\":39,\"format\":{}},{\"startIndex\":43,\"format\":{\"underline\":true}},{\"startIndex\":47,\"format\":{}}]}"

    @Test
    fun toJsonTest() {
        val cellStr = CellConverter.toJsonStr(CELL)
        println(cellStr)
        Assert.assertEquals(CELL_JSON_STR, cellStr)
    }

    @Test
    fun fromJsonTest() {
        val cell = CellConverter.fromJson(CELL_JSON_STR)
        println(cell)
        // {"formattedValue":"seasonal fruits","textFormatRuns":[{"format":{"underline":true}},{"startIndex":3,"format":{}}]}
        Assert.assertEquals(cell, CELL)
    }

    @Test
    fun getStyleListTest() {
        val cell = CellConverter.fromJson(JSON_CELL)
        val (text, retList) = CellConverter.getStyleItemPair(cell)
        println("text: $text")
        for (item in retList) {
            println(
                "[${item.start}:${item.end}) \"${
                    text.substring(
                        item.start,
                        item.end
                    )
                }\" ${item.spanStyle!!.background}"
            )
        }
    }
}