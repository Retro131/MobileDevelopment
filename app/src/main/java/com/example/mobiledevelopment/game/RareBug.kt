package com.example.mobiledevelopment.game

import com.example.mobiledevelopment.R
import com.example.mobiledevelopment.game.Movements.CircularMovement
import com.example.mobiledevelopment.game.Movements.MovementBehavior

data class RareBug(
    override val id: Int,
    override val x: Float,
    override val y: Float,

    override val velocityX: Float = 160f,
    override val velocityY: Float = 120f,

    override val movement: MovementBehavior = CircularMovement,

    override val timeUntilTurn: Float = movement.changeInterval,
    override val turnNumber: Int = 0
) : Bug() {

    override val size: Float = 130f
    override val points: Int = 50
    override val imageRes: Int = R.drawable.bug_rare

    protected override fun copyMotion(
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