package com.shimnssso.headonenglish

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shimnssso.headonenglish.repository.LectureRepository
import com.shimnssso.headonenglish.room.FakeData
import com.shimnssso.headonenglish.room.LectureDatabase
import com.shimnssso.headonenglish.room.MIGRATION_4_5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A very simple global singleton dependency graph.
 *
 * For a real app, you would use something like Hilt/Dagger instead.
 */
object Graph {
    lateinit var database: LectureDatabase
        private set

    val lectureRepository by lazy {
        LectureRepository(database)
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, LectureDatabase::class.java, "data.db")
            // https://stackoverflow.com/a/57570451/16111308
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        database.subjectDao.insertSubjects(FakeData.DEFAULT_SUBJECTS)
                        database.globalDao.insert(FakeData.DEFAULT_GLOBAL)
                    }
                }
            })
            .addMigrations(MIGRATION_4_5)
            .build()
    }
}