package com.example.mobiledevelopment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Insert
    suspend fun insert(player: PlayerEntity): Long

    @Query("SELECT * FROM players ORDER BY fullName, id")
    fun observeAll(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getById(id: Long): PlayerEntity?

    @Update
    suspend fun update(player: PlayerEntity): Int
}