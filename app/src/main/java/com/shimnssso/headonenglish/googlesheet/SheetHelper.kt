package com.shimnssso.headonenglish.googlesheet

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.CellData
import com.google.api.services.sheets.v4.model.GridData
import com.google.api.services.sheets.v4.model.RowData
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.SheetProperties
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.gson.GsonBuilder
import com.shimnssso.headonenglish.room.DatabaseCard
import com.shimnssso.headonenglish.room.DatabaseLecture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

object SheetHelper {
    private var drive: Drive? = null
    private var sheets: Sheets? = null

    private val gson = GsonBuilder().create()

    fun init(drive: Drive, sheets: Sheets) {
        this.drive = drive
        this.sheets = sheets
    }

    private fun isInitialized(): Boolean {
        return (drive != null) && (sheets != null)
    }

    fun getFilePickerIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.type = "application/vnd.google-apps.spreadsheet"

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        return intent
    }

    suspend fun getFilesFromUri(contentResolver: ContentResolver, uri: Uri): List<File> {
        if (!isInitialized()) {
            throw IOException("SheetHelper has not been initialized yet!!")
        }
        var retFiles: List<File> = listOf()
        withContext(Dispatchers.IO) {
            var fileName: String
            contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    fileName = cursor.getString(nameIndex)
                } else {
                    throw IOException("Empty cursor returned for file.")
                }
            }

            // Finding a Google Drive Sheet By Name (https://stackoverflow.com/a/43452641)
            val request: Drive.Files.List = drive!!.files().list()
                .setPageSize(10) // Available Query parameters here:
                //https://developers.google.com/drive/v3/web/search-parameters
                .setQ("mimeType = 'application/vnd.google-apps.spreadsheet' and name contains '$fileName' and trashed = false")
                .setFields("nextPageToken, files(id, name, owners, modifiedTime)")

            val result = request.execute()

            val files = result.files
            if (files != null) {
                retFiles = files
                for (file in files) {
                    Timber.e("file: $file")
                    Timber.e("file.modifiedTime: ${file.modifiedTime}")
                    Timber.e("file.owners ${file.owners}")
                    Timber.e("spreadsheetId: %s", file.id)
                }
            }
        }
        return retFiles
    }

    suspend fun fetchSpreadsheet(spreadsheetId: String): Spreadsheet {
        if (!isInitialized()) {
            throw IOException("SheetHelper has not been initialized yet!!")
        }

        var spreadsheet: Spreadsheet
        withContext(Dispatchers.IO) {
            spreadsheet = sheets!!.spreadsheets()
                .get(spreadsheetId)  // https://developers.google.com/sheets/api/reference/rest/v4/spreadsheets/get
                .setFields("sheets.properties,sheets.data.rowData.values.formattedValue,sheets.data.rowData.values.textFormatRuns")
                .execute()

            val data0: GridData = spreadsheet.sheets[0].data[0]
            Timber.e("data[0](${data0::class.simpleName}): $data0")

            val properties: SheetProperties = spreadsheet.sheets[0].properties
            Timber.e("properties(${properties::class.simpleName}): $properties")

            val rowData = data0.rowData
            for (row: RowData in rowData) {
                Timber.e("row(${row::class.simpleName}): $row")
            }
        }
        return spreadsheet
    }

    fun getLectureCardListPair(
        spreadsheet: Spreadsheet,
        subjectId: Int,
        remainedLectureMap: MutableMap<String, DatabaseLecture>,
        newLectures: MutableList<DatabaseLecture>,
        updateLectures: MutableList<DatabaseLecture>
    ): List<DatabaseCard> {
        val newCards = mutableListOf<DatabaseCard>()

        for (sheet: Sheet in spreadsheet.sheets) {
            val sheetProperties: SheetProperties = sheet.properties
            Timber.e("sheetProperties: $sheetProperties")
            val sheetTitle = sheetProperties.title
            if (sheetTitle.endsWith("_temp")) {
                Timber.d("skip $sheetTitle sheet")
                continue
            }

            val frozenRowCount = sheetProperties.gridProperties.frozenRowCount

            val data: GridData = sheet.data[0]  // We didn't query for multi section.
            val idxHolder = IndexHolder()

            val rowDataList: List<RowData> = data.rowData
            for ((i, rowData: RowData) in rowDataList.withIndex()) {
                val cells = rowData.getValues()
                if (i < frozenRowCount) {
                    idxHolder.setColumnIndices(rowData)
                } else if (cells[idxHolder.order].formattedValue == "0") {
                    val lecture = getLecture(idxHolder, cells, subjectId)
                    val originLecture = remainedLectureMap[lecture.date]
                    if (originLecture == null) {
                        newLectures.add(lecture)
                    } else {
                        val updateLecture: DatabaseLecture = originLecture.copy(title = lecture.title,
                            category = lecture.category,
                            remoteUrl = lecture.remoteUrl,
                            link1 = lecture.link1,
                            link2 = lecture.link2,
                        )
                        updateLectures.add(updateLecture)
                        remainedLectureMap.remove(lecture.date)
                    }
                } else {
                    val card = getCard(idxHolder, cells, subjectId)
                    newCards.add(card)
                }
            }
        }
        return newCards
    }

    private fun getLecture(idx: IndexHolder, cells: List<CellData>, subjectId: Int): DatabaseLecture {
        val category =
            if (idx.metaCategory > 0 && cells.size > idx.metaCategory) cells[idx.metaCategory].formattedValue else null
        val remoteUrl =
            if (idx.metaRemoteUrl > 0 && cells.size > idx.metaRemoteUrl) cells[idx.metaRemoteUrl].formattedValue else null
        val link1 =
            if (idx.metaLink1 > 0 && cells.size > idx.metaLink1) cells[idx.metaLink1].formattedValue else null
        val link2 =
            if (idx.metaLink2 > 0 && cells.size > idx.metaLink2) cells[idx.metaLink2].formattedValue else null

        return DatabaseLecture(
            subjectId = subjectId,
            date = cells[idx.date].formattedValue!!,
            title = cells[idx.metaTitle].formattedValue!!,
            category = category,
            remoteUrl = remoteUrl,
            localUrl = null,
            link1 = link1,
            link2 = link2,
        )
    }

    private fun getCard(idx: IndexHolder, cells: List<CellData>, subjectId: Int): DatabaseCard {
        val hint = if (idx.hint > 0 && cells.size > idx.hint) cells[idx.hint].formattedValue else null
        val note = if (idx.note > 0 && cells.size > idx.note) cells[idx.note].formattedValue else null
        val memo = if (idx.memo > 0 && cells.size > idx.memo) cells[idx.memo].formattedValue else null
        return DatabaseCard(
            subjectId = subjectId,
            date = cells[idx.date].formattedValue!!,
            order = cells[idx.order].formattedValue!!.toInt(),
            text = gson.toJson(cells[idx.text]),
            hint = hint,
            note = note,
            memo = memo,
        )
    }
}