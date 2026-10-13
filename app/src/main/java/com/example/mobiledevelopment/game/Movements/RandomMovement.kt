package com.example.mobiledevelopment.game.Movements

import kotlin.random.Random

object RandomMovement : MovementBehavior {
    override val changeInterval: Float = 1.2f

    override fun getTurnAngle(turnNumber: Int): Float {
        return Random.nextFloat() * 120f - 60f
    }
}