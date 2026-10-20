package com.example.mobiledevelopment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {

    @Insert
    suspend fun insert(result: GameResultEntity): Long

    @Query("""
        SELECT
            r.id,
            p.fullName AS playerName,
            r.score,
            r.difficulty,
            r.finishedAt
        FROM game_results AS r
        INNER JOIN players AS p ON p.id = r.playerId
        ORDER BY r.finishedAt DESC, r.id DESC
    """)
    fun observeResults(): Flow<List<GameResultRow>>

    @Query("""
        SELECT
            r.id,
            p.fullName AS playerName,
            r.score,
            r.difficulty,
            r.finishedAt
        FROM game_results AS r
        INNER JOIN players AS p ON p.id = r.playerId
        WHERE r.id = (
            SELECT candidate.id
            FROM game_results AS candidate
            WHERE candidate.playerId = r.playerId
            ORDER BY
                candidate.score DESC,
                candidate.finishedAt DESC,
                candidate.id DESC
            LIMIT 1
        )
        ORDER BY r.score DESC, r.finishedAt DESC, r.id DESC
    """)
    fun observeBestResults(): Flow<List<GameResultRow>>
}