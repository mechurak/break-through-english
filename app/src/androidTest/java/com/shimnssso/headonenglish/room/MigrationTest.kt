package com.shimnssso.headonenglish.room

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import timber.log.Timber
import java.io.IOException

class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        LectureDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate4To5() {
        val lectureCv1 = ContentValues()
        lectureCv1.put("subjectId", 1)
        lectureCv1.put("date", "2021-05-12")
        lectureCv1.put("title", "발음 강세 Unit 553. 체중")
        lectureCv1.put("category", "Maintaining Our Health")

        val lectureCv2 = ContentValues()
        lectureCv2.put("subjectId", 1)
        lectureCv2.put("date", "2021-05-13")
        lectureCv2.put("title", "발음 강세 Unit 554. Temp Title")
        lectureCv2.put("category", "Maintaining Our Health")

        var dbBefore = helper.createDatabase(TEST_DB, 4).apply {
            // db has schema version 4. insert some data using SQL queries.
            // You cannot use DAO classes because they expect the latest schema.

            insert("lecture_table", SQLiteDatabase.CONFLICT_REPLACE, lectureCv1)
            insert("lecture_table", SQLiteDatabase.CONFLICT_REPLACE, lectureCv2)

            // Prepare for the next version.
            close()
        }

        // Re-open the database with version 5 and provide
        // MIGRATION_4_5 as the migration process.
        val dbAfter = helper.runMigrationsAndValidate(TEST_DB, 5, true, MIGRATION_4_5)

        val cursor = dbAfter.query("SELECT * from lecture_table")
        val idxStudyDate = cursor.getColumnIndex("lastStudyDate")
        val idxStudyPoint = cursor.getColumnIndex("studyPoint")
        Timber.i("count: ${cursor.count}")
        Timber.i("idxStudyDate: $idxStudyDate")
        assertEquals(8, idxStudyDate)
        Timber.i("idxStudyPoint: $idxStudyPoint")
        assertEquals(9, idxStudyPoint)

        if (cursor.moveToFirst()) {
            do {
                Timber.i("== row ===")
                val subjectId = cursor.getInt(0)
                val date = cursor.getString(1)
                val title = cursor.getString(2)
                val category = cursor.getString(3)
                val remoteUrl = cursor.getStringOrNull(4)
                val localUrl = cursor.getStringOrNull(5)
                val link1 = cursor.getStringOrNull(6)
                val link2 = cursor.getStringOrNull(7)
                val studyDate = cursor.getInt(idxStudyDate)
                val studyPoint = cursor.getInt(idxStudyPoint)
                Timber.i("subjectId: $subjectId, date: $date")
                Timber.i("title: $title, category: $category")
                Timber.i("remoteUrl: $remoteUrl, localUrl: $localUrl")
                Timber.i("link1: $link1, link2: $link2")
                Timber.i("studyDate: $studyDate")
                Timber.i("studyPoint: $studyPoint")
            } while (cursor.moveToNext())
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate4To6() {
        val lectureCv1 = ContentValues()
        lectureCv1.put("subjectId", 1)
        lectureCv1.put("date", "2021-05-12")
        lectureCv1.put("title", "발음 강세 Unit 553. 체중")
        lectureCv1.put("category", "Maintaining Our Health")

        val lectureCv2 = ContentValues()
        lectureCv2.put("subjectId", 1)
        lectureCv2.put("date", "2021-05-13")
        lectureCv2.put("title", "발음 강세 Unit 554. Temp Title")
        lectureCv2.put("category", "Maintaining Our Health")

        var dbBefore = helper.createDatabase(TEST_DB, 4).apply {
            // db has schema version 4. insert some data using SQL queries.
            // You cannot use DAO classes because they expect the latest schema.

            insert("lecture_table", SQLiteDatabase.CONFLICT_REPLACE, lectureCv1)
            insert("lecture_table", SQLiteDatabase.CONFLICT_REPLACE, lectureCv2)

            // Prepare for the next version.
            close()
        }

        val dbAfter = helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_4_5, MIGRATION_5_6)

        val cursor = dbAfter.query("SELECT * from lecture_table")
        val idxStudyDate = cursor.getColumnIndex("lastStudyDate")
        val idxStudyPoint = cursor.getColumnIndex("studyPoint")
        val idxQuizCount = cursor.getColumnIndex("quizCount")
        Timber.i("count: ${cursor.count}")
        Timber.i("idxStudyDate: $idxStudyDate")
        assertEquals(8, idxStudyDate)
        Timber.i("idxStudyPoint: $idxStudyPoint")
        assertEquals(9, idxStudyPoint)
        Timber.i("idxQuizCount: $idxQuizCount")
        assertEquals(10, idxQuizCount)

        if (cursor.moveToFirst()) {
            do {
                Timber.i("== row ===")
                val subjectId = cursor.getInt(0)
                val date = cursor.getString(1)
                val title = cursor.getString(2)
                val category = cursor.getString(3)
                val remoteUrl = cursor.getStringOrNull(4)
                val localUrl = cursor.getStringOrNull(5)
                val link1 = cursor.getStringOrNull(6)
                val link2 = cursor.getStringOrNull(7)
                val studyDate = cursor.getInt(idxStudyDate)
                val studyPoint = cursor.getInt(idxStudyPoint)
                val quizCount = cursor.getInt(idxQuizCount)
                Timber.i("subjectId: $subjectId, date: $date")
                Timber.i("title: $title, category: $category")
                Timber.i("remoteUrl: $remoteUrl, localUrl: $localUrl")
                Timber.i("link1: $link1, link2: $link2")
                Timber.i("studyDate: $studyDate")
                Timber.i("studyPoint: $studyPoint")
                Timber.i("quizCount: $quizCount")
            } while (cursor.moveToNext())
        }
    }

}