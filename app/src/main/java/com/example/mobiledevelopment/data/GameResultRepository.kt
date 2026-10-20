package com.example.mobiledevelopment.data

import com.example.mobiledevelopment.model.GameSettings

class GameResultRepository(
    private val gameResultDao: GameResultDao
) {

    suspend fun saveResult(
        playerId: Long,
        score: Int,
        settings: GameSettings
    ): Long {
        require(playerId > 0L) {
            "Перед сохранением результата выберите игрока"
        }

        val result = GameResultEntity(
            playerId = playerId,
            score = score,
            difficulty = settings.difficulty.name,
            speed = settings.speed,
            maxCockroaches = settings.maxCockroaches,
            bonusInterval = settings.bonusInterval,
            roundDuration = settings.roundDuration
        )

        return gameResultDao.insert(result)
    }
}