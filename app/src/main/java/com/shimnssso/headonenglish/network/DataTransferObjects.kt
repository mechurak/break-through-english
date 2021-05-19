package com.shimnssso.headonenglish.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BriefInfoResponse(
    val properties: DocProps,
    val sheets: List<SheetInfo>,
)

@JsonClass(generateAdapter = true)
data class DocProps(
    val title: String,
)

@JsonClass(generateAdapter = true)
data class SheetInfo(
    val properties: SheetInfoProps,
    val data: List<SheetData>,
)

@JsonClass(generateAdapter = true)
data class SheetInfoProps(
    val sheetId: Int,
    val title: String,
    val index: Int,
    val sheetType: String,
    val gridProperties: GridProps
)

@JsonClass(generateAdapter = true)
data class GridProps(
    val rowCount: Int,
    val columnCount: Int,
    val frozenRowCount: Int,
)


@JsonClass(generateAdapter = true)
data class RawDataResponse(
    val sheets: List<SheetInfo>,
)

@JsonClass(generateAdapter = true)
data class SheetData(
    val rowData: List<RowDataItem>,
)

@JsonClass(generateAdapter = true)
data class RowDataItem(
    val values: List<Cell>,
)

@JsonClass(generateAdapter = true)
data class Cell(
    val formattedValue: String,
    val textFormatRuns: List<TextFormat>?
)

@JsonClass(generateAdapter = true)
data class TextFormat(
    val startIndex: Int?,
    val format: Format,
)

@JsonClass(generateAdapter = true)
data class Format(
    val underline: Boolean?,
    val bold: Boolean?,
)
