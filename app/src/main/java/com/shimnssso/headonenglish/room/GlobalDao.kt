package com.shimnssso.headonenglish.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GlobalDao {
    @Query("select * from global_table limit 1")
    suspend fun getGlobal(): DatabaseGlobal

    @Query("select * from global_table limit 1")
    fun currentData(): LiveData<DatabaseGlobal>

    @Insert
    suspend fun insert(databaseGlobal: DatabaseGlobal)

    @Update
    suspend fun update(databaseGlobal: DatabaseGlobal)
}