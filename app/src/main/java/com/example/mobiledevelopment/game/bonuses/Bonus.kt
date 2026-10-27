package com.example.mobiledevelopment.game.bonuses

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.example.mobiledevelopment.game.bonuses.movements.BonusMovement

abstract class Bonus(
    val x: Float,
    val y: Float,
    val radius: Float = 45f
) {
    @get:DrawableRes
    abstract val imageRes: Int

    abstract val movement: BonusMovement

    open val durationSeconds: Float = 5f

    @get:RawRes
    open val soundRes: Int? = null

    fun contains(touchX: Float, touchY: Float): Boolean {
        val dx = touchX - x
        val dy = touchY - y

        return dx * dx + dy * dy <= radius * radius
    }
}