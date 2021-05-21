package com.shimnssso.headonenglish

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shimnssso.headonenglish.network.MyGoogleSheetService
import com.shimnssso.headonenglish.repository.LectureRepository
import com.shimnssso.headonenglish.room.DatabaseGlobal
import com.shimnssso.headonenglish.room.DatabaseSubject
import com.shimnssso.headonenglish.room.LectureDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            // This is not recommended for normal apps, but the goal of this sample isn't to
            // showcase all of Room.
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        database.subjectDao.insert(
                            DatabaseSubject(
                                0,
                                "정면돌파 스피킹",
                                0,
                                "https://home.ebse.co.kr/10mins_lee2/",
                                "1veQzV0fyYHO_4Lu2l33ZRXbjy47_q8EI1nwVAQXJcVQ",
                                true
                            )
                        )
                        database.subjectDao.insert(
                            DatabaseSubject(
                                1,
                                "입트영 최고의 스피킹 60",
                                0,
                                "",
                                "1GeK1Kz8GycGMYviq52sqV3-WKoI8Gw7llSOvJekp01s",
                                false
                            )
                        )
                        database.globalDao.insert(DatabaseGlobal(0))
                    }
                }
            })
            .build()
    }
}