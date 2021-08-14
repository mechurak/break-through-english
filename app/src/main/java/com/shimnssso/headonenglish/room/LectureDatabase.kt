package com.shimnssso.headonenglish.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [DatabaseSubject::class, DatabaseGlobal::class, DatabaseLecture::class, DatabaseCard::class],
    version = 5,
    exportSchema = true
)
abstract class LectureDatabase : RoomDatabase() {
    abstract val subjectDao: SubjectDao
    abstract val globalDao: GlobalDao
    abstract val lectureDao: LectureDao
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE lecture_table ADD COLUMN lastStudyDate INTEGER default 0 NOT NULL")
        database.execSQL("ALTER TABLE lecture_table ADD COLUMN studyPoint INTEGER default 0 NOT NULL")
        database.execSQL("ALTER TABLE card_table ADD COLUMN isForQuiz INTEGER default 0 NOT NULL")
    }
}