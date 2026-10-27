package com.example.mobiledevelopment.game.bonuses.movements

import androidx.compose.ui.geometry.Offset
import com.example.mobiledevelopment.game.Bug

interface BonusMovement {

    fun nextPosition(
        bug: Bug,
        deltaSeconds: Float,
        tilt: Offset
    ): Offset
}