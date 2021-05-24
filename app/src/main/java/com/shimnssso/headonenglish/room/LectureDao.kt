package com.shimnssso.headonenglish.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LectureDao {
    @Query("select * from lecture_table where subjectId = :subjectId order by date desc")
    fun getLectures(subjectId: Int): LiveData<List<DatabaseLecture>>

    @Query("select * from lecture_table where date = :date limit 1")
    fun getLecture(date: String): LiveData<DatabaseLecture>

    @Query("select * from card_table where date = :date and subjectId = :subjectId")
    fun getCards(subjectId: Int, date: String): LiveData<List<DatabaseCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(rows: List<DatabaseCard>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLectures(lectures: List<DatabaseLecture>)

    @Delete
    suspend fun deleteLecture(lecture: DatabaseLecture)

    @Query("delete from card_table where date = :date")
    suspend fun deleteCards(date: String)
}