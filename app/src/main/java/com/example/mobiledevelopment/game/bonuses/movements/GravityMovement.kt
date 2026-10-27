package com.example.mobiledevelopment.game.bonuses.movements

import androidx.compose.ui.geometry.Offset
import com.example.mobiledevelopment.game.Bug

class GravityMovement(
    private val speed: Float = 700f
) : BonusMovement {

    override fun nextPosition(
        bug: Bug,
        deltaSeconds: Float,
        tilt: Offset
    ): Offset {
        return Offset(
            x = bug.x + tilt.x * speed * deltaSeconds,
            y = bug.y + tilt.y * speed * deltaSeconds
        )
    }
}