package com.example.mobiledevelopment.game

import com.example.mobiledevelopment.game.Movements.CircularMovement
import com.example.mobiledevelopment.game.Movements.RandomMovement
import com.example.mobiledevelopment.game.Movements.ZigzagMovement
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