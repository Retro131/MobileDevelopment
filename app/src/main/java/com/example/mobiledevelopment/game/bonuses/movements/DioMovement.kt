package com.example.mobiledevelopment.game.bonuses.movements

import androidx.compose.ui.geometry.Offset
import com.example.mobiledevelopment.game.Bug
import kotlin.math.min
import kotlin.math.sqrt

class DioMovement : BonusMovement {

    override fun nextPosition(
        bug: Bug,
        deltaSeconds: Float,
        tilt: Offset
    ): Offset {

        return Offset(
            x = bug.x,
            y = bug.y
        )
    }
}