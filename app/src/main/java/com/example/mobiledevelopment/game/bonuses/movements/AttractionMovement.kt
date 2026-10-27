package com.example.mobiledevelopment.game.bonuses.movements

import androidx.compose.ui.geometry.Offset
import com.example.mobiledevelopment.game.Bug
import kotlin.math.min
import kotlin.math.sqrt

class AttractionMovement(
    private val targetX: Float,
    private val targetY: Float,
    private val speed: Float = 500f,
) : BonusMovement {

    override fun nextPosition(
        bug: Bug,
        deltaSeconds: Float,
        tilt: Offset
    ): Offset {
        val dx = targetX - bug.x
        val dy = targetY - bug.y

        val distance = sqrt(dx * dx + dy * dy)

        if (distance == 0f) {
            return Offset(bug.x, bug.y)
        }

        val step = min(
            speed * deltaSeconds,
            distance
        )

        return Offset(
            x = bug.x + dx / distance * step,
            y = bug.y + dy / distance * step
        )
    }
}