package com.example.mobiledevelopment.game

import com.example.mobiledevelopment.R
import com.example.mobiledevelopment.game.Movements.MovementBehavior
import com.example.mobiledevelopment.game.Movements.RandomMovement

data class GoldBug(
    override val id: Int,
    override val x: Float,
    override val y: Float,

    override val points: Int,

    override val velocityX: Float = 160f,
    override val velocityY: Float = 120f,

    override val movement: MovementBehavior = RandomMovement,

    override val timeUntilTurn: Float = movement.changeInterval,
    override val turnNumber: Int = 0
) : Bug() {

    override val size: Float = 140f

    override val imageRes: Int = R.drawable.bug_gold

    override fun copyMotion(
        x: Float,
        y: Float,
        velocityX: Float,
        velocityY: Float,
        timeUntilTurn: Float,
        turnNumber: Int
    ): Bug {
        return copy(
            x = x,
            y = y,
            velocityX = velocityX,
            velocityY = velocityY,
            timeUntilTurn = timeUntilTurn,
            turnNumber = turnNumber
        )
    }
}