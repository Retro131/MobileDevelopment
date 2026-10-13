package com.example.mobiledevelopment.game.Movements

interface MovementBehavior {
    val changeInterval: Float

    fun getTurnAngle(turnNumber: Int): Float
}
