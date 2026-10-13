package com.example.mobiledevelopment.game.Movements

object ZigzagMovement : MovementBehavior {
    override val changeInterval: Float = 0.7f

    override fun getTurnAngle(turnNumber: Int): Float {
        return if (turnNumber % 2 == 0) {
            90f
        } else {
            -90f
        }
    }
}