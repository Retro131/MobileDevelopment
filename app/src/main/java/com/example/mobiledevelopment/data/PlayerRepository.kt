package com.example.mobiledevelopment.data

import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerRepository(
    private val playerDao: PlayerDao
) {

    fun observePlayers(): Flow<List<Player>> {
        return playerDao.observeAll().map { entities ->
            entities.map { entity ->
                entity.toPlayer()
            }
        }
    }

    suspend fun register(
        player: Player,
        settings: GameSettings
    ): Player {
        require(player.fullName.isNotBlank()) {
            "Введите ФИО"
        }

        require(player.id == 0L) {
            "Этот пользователь уже зарегистрирован"
        }

        val entity = player.toEntity(settings)
        val id = playerDao.insert(entity)

        return entity.copy(id = id).toPlayer()
    }

    suspend fun getById(
        id: Long
    ): Pair<Player, GameSettings>? {
        val entity = playerDao.getById(id) ?: return null

        return entity.toPlayer() to entity.toSettings()
    }

    suspend fun update(
        player: Player,
        settings: GameSettings
    ) {
        require(player.id > 0L) {
            "Пользователь ещё не зарегистрирован"
        }

        require(player.fullName.isNotBlank()) {
            "Введите ФИО"
        }

        val entity = player.toEntity(settings)
        val updatedRows = playerDao.update(entity)

        check(updatedRows == 1) {
            "Пользователь не найден в базе"
        }
    }
}