package com.example.mobiledevelopment.data

data class GameResultRow(
    val id: Long,
    val playerName: String,
    val score: Int,
    val difficulty: String,
    val finishedAt: Long
)