package com.example.mobiledevelopment.game.bonuses

import androidx.annotation.RawRes
import com.example.mobiledevelopment.R
import com.example.mobiledevelopment.game.bonuses.movements.AttractionMovement

class AttractionBonus(
    x: Float,
    y: Float,
    radius: Float = 60f,
    override val durationSeconds: Float = 5f,
    @get:RawRes
    override val soundRes: Int? = R.raw.hand,
    speed: Float = 1500f
) : Bonus(
    x = x,
    y = y,
    radius = radius
) {
    override val imageRes: Int = R.drawable.enigma

    override val movement = AttractionMovement(
        targetX = x,
        targetY = y,
        speed = speed
    )
}