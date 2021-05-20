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
    fun toSourceListTest() {
        val formattedSourceList = CellConverter.toSourceList(CELL)
        println(formattedSourceList)
    }
}