package com.shimnssso.headonenglish.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubjectDao {
    @Query("select * from subject_table where subjectId = :subjectId limit 1")
    suspend fun getSubject(subjectId: Int): DatabaseSubject

    @Query("select * from subject_table where subjectId = :subjectId limit 1")
    fun currentSubject(subjectId: Int): LiveData<DatabaseSubject?>

    @Query("select * from subject_table")
    fun getSubjects(): LiveData<List<DatabaseSubject>>

    @Query("select subjectId from subject_table where rowId = :rowId")
    suspend fun getSubjectId(rowId: Long): Long

    @Query("select * from subject_table limit 1")
    suspend fun getFirstSubject(): DatabaseSubject

    @Insert
    suspend fun insert(subject: DatabaseSubject): Long

    @Insert
    suspend fun insertSubjects(subjects: List<DatabaseSubject>)

    @Update
    suspend fun update(subject: DatabaseSubject)

    @Query("delete from subject_table where subjectId = :subjectId")
    suspend fun deleteSubject(subjectId: Int)
}