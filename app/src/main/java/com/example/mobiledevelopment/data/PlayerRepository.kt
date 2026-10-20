package com.example.mobiledevelopment.data

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

    suspend fun register(player: Player): Player {
        require(player.fullName.isNotBlank()) {
            "Введите ФИО"
        }
        require(player.id == 0L) {
            "Этот пользователь уже зарегистрирован"
        }

        val entity = player.toEntity()
        val id = playerDao.insert(entity)

        return entity.copy(id = id).toPlayer()
    }

    suspend fun getById(
        id: Long
    ): Player? {
        val entity = playerDao.getById(id) ?: return null

        return entity.toPlayer()
    }

    suspend fun update(player: Player) {
        require(player.id > 0L) {
            "Пользователь ещё не зарегистрирован"
        }

        require(player.fullName.isNotBlank()) {
            "Введите ФИО"
        }

        val entity = player.toEntity()
        val updatedRows = playerDao.update(entity)

        check(updatedRows == 1) {
            "Пользователь не найден в базе"
        }
    }
    suspend fun createGuest(): Player {
        val entity = PlayerEntity(
            fullName = "Гость",
            isGuest = true
        )

        val id = playerDao.insert(entity)

        val guest = entity.copy(
            id = id,
            fullName = "Гость($id)"
        )

        playerDao.update(guest)

        return guest.toPlayer()
    }
}