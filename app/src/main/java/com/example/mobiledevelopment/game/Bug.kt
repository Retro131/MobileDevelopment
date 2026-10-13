package com.example.mobiledevelopment.game

import androidx.annotation.DrawableRes

abstract class Bug {
    abstract val id: Int
    abstract val x: Float
    abstract val y: Float

    abstract val size: Float
    abstract val points: Int

    @get:DrawableRes
    abstract val imageRes: Int

    val radius: Float
        get() = size / 2f

    fun contains(touchX: Float, touchY: Float): Boolean {
        val dx = touchX - x
        val dy = touchY - y

        return dx * dx + dy * dy <= radius * radius
    }
}