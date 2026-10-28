package com.example.mobiledevelopment.game

import com.example.mobiledevelopment.game.Movements.CircularMovement
import com.example.mobiledevelopment.game.Movements.RandomMovement
import com.example.mobiledevelopment.game.Movements.ZigzagMovement
import com.example.mobiledevelopment.game.bonuses.AttractionBonus
import com.example.mobiledevelopment.game.bonuses.Bonus
import com.example.mobiledevelopment.game.bonuses.DioBonus
import com.example.mobiledevelopment.game.bonuses.GravityBonus
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object BugFactory {

    private enum class BugType {
        DEFAULT,
        FAST,
        RARE
    }

    fun createGold(
        id: Int,
        fieldWidth: Float,
        fieldHeight: Float,
        speedMultiplier: Int,
        points: Int
    ): GoldBug {
        val size = 140f
        val radius = size / 2f

        require(fieldWidth >= size && fieldHeight >= size) {
            "Поле слишком маленькое для золотого таракана"
        }

        require(speedMultiplier > 0) {
            "Множитель скорости должен быть положительным"
        }

        require(points > 0) {
            "Количество очков должно быть положительным"
        }

        val x = radius + Random.nextFloat() *
                (fieldWidth - size)

        val y = radius + Random.nextFloat() *
                (fieldHeight - size)

        val angle = Random.nextFloat() * 2f * PI.toFloat()
        val speed = 200f * speedMultiplier

        return GoldBug(
            id = id,
            x = x,
            y = y,
            velocityX = cos(angle) * speed,
            velocityY = sin(angle) * speed,
            points = points
        )
    }

    fun createBonus(
        fieldWidth: Float,
        fieldHeight: Float,
    ) : Bonus {
        val radius = 45f

        val bonusX = radius +
                Random.nextFloat() * (fieldWidth - radius * 2f)

        val bonusY = radius +
                Random.nextFloat() * (fieldHeight - radius * 2f)

        val randomValue = Random.nextInt(3)

        val fieldBonus = if (randomValue == 0) {
            GravityBonus(
                x = bonusX,
                y = bonusY,
                radius = radius,
            )
        } else if (randomValue == 1) {
            AttractionBonus(
                x = bonusX,
                y = bonusY,
                radius = radius
            )
        } else {
            DioBonus(
                x = bonusX,
                y = bonusY,
                radius = radius
            )
        }

        return fieldBonus
    }

    fun create(
        id: Int,
        fieldWidth: Float,
        fieldHeight: Float,
        speedMultiplier: Int
    ): Bug {
        val roll = Random.nextInt(100)

        val type = when {
            roll < 65 -> BugType.DEFAULT
            roll < 95 -> BugType.FAST
            else -> BugType.RARE
        }

        val size = when (type) {
            BugType.DEFAULT -> 140f
            BugType.FAST -> 110f
            BugType.RARE -> 130f
        }

        val baseSpeed = when (type) {
            BugType.DEFAULT -> 150f
            BugType.FAST -> 300f
            BugType.RARE -> 200f
        }

        val radius = size / 2f

        val x = radius + Random.nextFloat() *
                (fieldWidth - size).coerceAtLeast(0f)

        val y = radius + Random.nextFloat() *
                (fieldHeight - size).coerceAtLeast(0f)

        val angle = Random.nextFloat() * 2f * PI.toFloat()
        val speed = baseSpeed * speedMultiplier

        val velocityX = cos(angle) * speed
        val velocityY = sin(angle) * speed

        return when (type) {
            BugType.DEFAULT -> DefaultBug(
                id = id,
                x = x,
                y = y,
                velocityX = velocityX,
                velocityY = velocityY,
                movement = when (Random.nextInt(3)) {
                    0 -> RandomMovement
                    1 -> ZigzagMovement
                    else -> CircularMovement
                }
            )

            BugType.FAST -> FastBug(
                id = id,
                x = x,
                y = y,
                velocityX = velocityX,
                velocityY = velocityY
            )

            BugType.RARE -> RareBug(
                id = id,
                x = x,
                y = y,
                velocityX = velocityX,
                velocityY = velocityY
            )
        }
    }
}