package com.example.mobiledevelopment.game.bonuses

import androidx.annotation.RawRes
import com.example.mobiledevelopment.R
import com.example.mobiledevelopment.game.bonuses.movements.GravityMovement

class GravityBonus(
    x: Float,
    y: Float,
    radius: Float = 60f,
    override val durationSeconds: Float = 5f,
    @get:RawRes
    override val soundRes: Int? = R.raw.star_platinum,
    speed: Float = 3000f
) : Bonus(
    x = x,
    y = y,
    radius = radius
) {
    override val imageRes: Int = R.drawable.gravity

    override val movement = GravityMovement(speed)
}