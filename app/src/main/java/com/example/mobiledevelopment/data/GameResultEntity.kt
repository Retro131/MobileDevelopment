package com.example.mobiledevelopment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_results")
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val playerId: Long,
    val score: Int,
    val difficulty: String,

    val speed: Int,
    val maxCockroaches: Int,
    val bonusInterval: Int,
    val roundDuration: Int,

    val finishedAt: Long = System.currentTimeMillis()
)