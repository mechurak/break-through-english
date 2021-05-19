package com.shimnssso.headonenglish.network

import com.shimnssso.headonenglish.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A retrofit service to fetch sheet data.
 */
interface MyGoogleSheetService {
    @GET(SHEET_ID)
    suspend fun getBriefInfo(
        @Query("fields") fields: String = FIELDS_FOR_BRIEF,
        @Query("key") key: String = BuildConfig.SHEET_API_KEY,
    ): BriefInfoResponse


    @GET(SHEET_ID)
    suspend fun getRawData(
        @Query("fields") fields: String = FIELDS_FOR_RAW,
        @Query("ranges") ranges: String = "2021-05",
        @Query("key") key: String = BuildConfig.SHEET_API_KEY,
    ): RawDataResponse

    companion object {
        private const val SHEET_ID = "1veQzV0fyYHO_4Lu2l33ZRXbjy47_q8EI1nwVAQXJcVQ"
        private const val FIELDS_FOR_BRIEF = "sheets.properties,properties.title"
        private const val FIELDS_FOR_RAW = "sheets.properties,sheets.data.rowData.values.formattedValue,sheets.data.rowData.values.textFormatRuns"
    }
}


/**
 * Main entry point for network access. Call like `SheetNetwork.sheetApi.getBriefInfo()`
 */
object SheetNetwork {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sheets.googleapis.com/v4/spreadsheets/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val sheetApi: MyGoogleSheetService = retrofit.create(MyGoogleSheetService::class.java)
}