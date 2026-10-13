package com.example.mobiledevelopment.game.Movements

object CircularMovement : MovementBehavior {
    override val changeInterval: Float = 0.25f

    override fun getTurnAngle(turnNumber: Int): Float {
        return 20f
    }
}