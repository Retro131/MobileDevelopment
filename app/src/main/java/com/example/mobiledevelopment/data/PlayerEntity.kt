package com.example.mobiledevelopment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val fullName: String,
    val gender: String,
    val course: Int,
    val birthDate: String,

    val difficulty: String,
    val speed: Int,
    val maxCockroaches: Int,
    val bonusInterval: Int,
    val roundDuration: Int
)