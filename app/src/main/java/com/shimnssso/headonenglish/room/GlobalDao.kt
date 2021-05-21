package com.shimnssso.headonenglish.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GlobalDao {
    @Query("select * from global_table limit 1")
    suspend fun getGlobal(): DatabaseGlobal

    @Insert
    suspend fun insert(databaseGlobal: DatabaseGlobal)

    @Update
    suspend fun update(databaseGlobal: DatabaseGlobal)
}