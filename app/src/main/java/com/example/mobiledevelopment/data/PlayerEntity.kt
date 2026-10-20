package com.example.mobiledevelopment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val fullName: String,
    val gender: String? = null,
    val course: Int? = null,
    val birthDate: String? = null,
    val isGuest: Boolean = false,
)