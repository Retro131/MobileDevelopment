package com.example.mobiledevelopment.game

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import com.example.mobiledevelopment.game.Movements.MovementBehavior
import com.example.mobiledevelopment.game.bonuses.movements.BonusMovement
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

abstract class Bug {
    abstract val id: Int

    abstract val x: Float
    abstract val y: Float

    abstract val velocityX: Float
    abstract val velocityY: Float

    abstract val size: Float
    abstract val points: Int

    @get:DrawableRes
    abstract val imageRes: Int

    abstract val movement: MovementBehavior

    abstract val timeUntilTurn: Float
    abstract val turnNumber: Int

    val radius: Float
        get() = size / 2f

    fun contains(touchX: Float, touchY: Float): Boolean {
        val dx = touchX - x
        val dy = touchY - y

        return dx * dx + dy * dy <= radius * radius
    }

    fun move(
        deltaSeconds: Float,
        fieldWidth: Float,
        fieldHeight: Float,
        bonusMovement: BonusMovement? = null,
        tilt: Offset = Offset.Zero
    ): Bug {
        if (!deltaSeconds.isFinite() || deltaSeconds <= 0f) {
            return this
        }

        val minX = radius
        val maxX = fieldWidth - radius

        val minY = radius
        val maxY = fieldHeight - radius

        if (maxX < minX || maxY < minY) {
            return this
        }

        if (bonusMovement != null) {
            val nextPosition = bonusMovement.nextPosition(
                bug = this,
                deltaSeconds = deltaSeconds,
                tilt = tilt
            )

            if (!nextPosition.x.isFinite() || !nextPosition.y.isFinite()) {
                return this
            }

            return copyMotion(
                x = nextPosition.x.coerceIn(minX, maxX),
                y = nextPosition.y.coerceIn(minY, maxY),
                velocityX = velocityX,
                velocityY = velocityY,
                timeUntilTurn = timeUntilTurn,
                turnNumber = turnNumber
            )
        }

        var nextVelocityX = velocityX
        var nextVelocityY = velocityY

        var nextTimeUntilTurn = timeUntilTurn - deltaSeconds
        var nextTurnNumber = turnNumber

        if (nextTimeUntilTurn <= 0f) {
            val angleDegrees = movement.getTurnAngle(nextTurnNumber)
            val angleRadians = angleDegrees * PI.toFloat() / 180f

            val oldVelocityX = nextVelocityX
            val oldVelocityY = nextVelocityY

            nextVelocityX =
                oldVelocityX * cos(angleRadians) -
                        oldVelocityY * sin(angleRadians)

            nextVelocityY =
                oldVelocityX * sin(angleRadians) +
                        oldVelocityY * cos(angleRadians)

            nextTimeUntilTurn += movement.changeInterval
            nextTurnNumber++
        }

        var nextX = x + nextVelocityX * deltaSeconds
        var nextY = y + nextVelocityY * deltaSeconds

        if (nextX < minX) {
            nextX = minX
            nextVelocityX = abs(nextVelocityX)
        } else if (nextX > maxX) {
            nextX = maxX
            nextVelocityX = -abs(nextVelocityX)
        }

        if (nextY < minY) {
            nextY = minY
            nextVelocityY = abs(nextVelocityY)
        } else if (nextY > maxY) {
            nextY = maxY
            nextVelocityY = -abs(nextVelocityY)
        }

        return copyMotion(
            x = nextX,
            y = nextY,
            velocityX = nextVelocityX,
            velocityY = nextVelocityY,
            timeUntilTurn = nextTimeUntilTurn,
            turnNumber = nextTurnNumber
        )
    }

    protected abstract fun copyMotion(
        x: Float,
        y: Float,
        velocityX: Float,
        velocityY: Float,
        timeUntilTurn: Float,
        turnNumber: Int
    ): Bug
}