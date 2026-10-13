package com.example.mobiledevelopment.game

import com.example.mobiledevelopment.R
import com.example.mobiledevelopment.game.Movements.MovementBehavior
import com.example.mobiledevelopment.game.Movements.ZigzagMovement

data class FastBug(
    override val id: Int,
    override val x: Float,
    override val y: Float,

    override val velocityX: Float = 240f,
    override val velocityY: Float = 180f,

    override val movement: MovementBehavior = ZigzagMovement,

    override val timeUntilTurn: Float = movement.changeInterval,
    override val turnNumber: Int = 0
) : Bug() {

    override val size: Float = 110f
    override val points: Int = 20
    override val imageRes: Int = R.drawable.bug_fast

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