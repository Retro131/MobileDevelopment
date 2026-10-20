package com.example.mobiledevelopment.data

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface GameResultDao {

    @Insert
    suspend fun insert(result: GameResultEntity): Long
}