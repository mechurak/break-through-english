package com.shimnssso.headonenglish.network

import com.shimnssso.headonenglish.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * A retrofit service to fetch sheet data.
 */
interface MyGoogleSheetService {
    @GET("{sheetId}")
    suspend fun getBriefInfo(
        @Path("sheetId") sheetId: String,
        @Query("fields") fields: String = FIELDS_FOR_BRIEF,
        @Query("key") key: String = BuildConfig.SHEET_API_KEY,
    ): BriefInfoResponse


    @GET("{sheetId}")
    suspend fun getRawData(
        @Path("sheetId") sheetId: String,
        @Query("fields") fields: String = FIELDS_FOR_RAW,
        @Query("ranges") ranges: String = "2021-05",
        @Query("key") key: String = BuildConfig.SHEET_API_KEY,
    ): RawDataResponse

    companion object {
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