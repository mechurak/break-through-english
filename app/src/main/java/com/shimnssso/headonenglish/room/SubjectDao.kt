package com.shimnssso.headonenglish.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface SubjectDao {
    @Query("select * from subject_table where subjectId = :subjectId limit 1")
    suspend fun getSubject(subjectId: Int): DatabaseSubject

    @Insert
    suspend fun insert(subject: DatabaseSubject)

    @Update
    suspend fun update(subejct: DatabaseSubject)
}