package com.shimnssso.headonenglish

import android.content.Context
import androidx.room.Room
import com.shimnssso.headonenglish.network.MyGoogleSheetService
import com.shimnssso.headonenglish.repository.LectureRepository
import com.shimnssso.headonenglish.room.LectureDatabase
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * A very simple global singleton dependency graph.
 *
 * For a real app, you would use something like Hilt/Dagger instead.
 */
object Graph {
    lateinit var retrofit: MyGoogleSheetService

    lateinit var database: LectureDatabase
        private set

    val lectureRepository by lazy {
        LectureRepository(database, retrofit)
    }

    fun provide(context: Context) {
        retrofit = Retrofit.Builder()
            .baseUrl("https://sheets.googleapis.com/v4/spreadsheets/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(MyGoogleSheetService::class.java)

        database = Room.databaseBuilder(context, LectureDatabase::class.java, "data.db")
            .createFromAsset("data.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}