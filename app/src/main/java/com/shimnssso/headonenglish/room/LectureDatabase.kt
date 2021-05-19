package com.shimnssso.headonenglish.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GlobalInfo::class, DatabaseLecture::class, DatabaseCard::class],
    version = 3,
    exportSchema = false
)
abstract class LectureDatabase : RoomDatabase() {
    abstract val lectureDao: LectureDao
}