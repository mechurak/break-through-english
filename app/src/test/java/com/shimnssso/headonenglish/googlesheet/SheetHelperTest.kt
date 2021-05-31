package com.shimnssso.headonenglish.googlesheet

import com.google.api.services.sheets.v4.model.CellData
import com.google.api.services.sheets.v4.model.TextFormat
import com.google.api.services.sheets.v4.model.TextFormatRun
import com.google.gson.GsonBuilder
import org.junit.Test

class SheetHelperTest {
    private val gson = GsonBuilder().create()

    companion object {
        private const val CELL_JSON_STR =
            "{\"formattedValue\":\"seasonal fruits\",\"textFormatRuns\":[{\"format\":{\"underline\":true}},{\"startIndex\":3,\"format\":{}}]}"
        private val CELL = CellData()
        init {
            CELL.formattedValue = "seasonal fruits"
            CELL.textFormatRuns = mutableListOf()

            val newFormat1 = TextFormat()
            newFormat1.underline = true
            val newFormatRun1 = TextFormatRun()
            newFormatRun1.format = newFormat1
            CELL.textFormatRuns.add(newFormatRun1)

            val newFormat2 = TextFormat()
            val newFormatRun2 = TextFormatRun()
            newFormatRun2.startIndex = 3
            newFormatRun2.format = newFormat2
            CELL.textFormatRuns.add(newFormatRun2)
        }
    }

    @Test
    fun fromJsonTest() {
        val cellData = gson.fromJson(CELL_JSON_STR, CellData::class.java)
        println("cellData(${cellData::class.simpleName}): $cellData")
        println("cellData(${cellData::class.simpleName}).toPrettyString(): ${cellData.toPrettyString()}")
    }

    @Test
    fun toJsonTest() {
        val cellJson = gson.toJson(CELL)
        println("cellJson(${cellJson::class.simpleName}): $cellJson")
    }
}