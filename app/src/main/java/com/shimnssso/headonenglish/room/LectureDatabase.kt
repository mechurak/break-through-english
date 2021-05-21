package com.shimnssso.headonenglish.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DatabaseSubject::class, DatabaseGlobal::class, DatabaseLecture::class, DatabaseCard::class],
    version = 4,
    exportSchema = false
)
abstract class LectureDatabase : RoomDatabase() {
    abstract val subjectDao: SubjectDao
    abstract val globalDao: GlobalDao
    abstract val lectureDao: LectureDao
}