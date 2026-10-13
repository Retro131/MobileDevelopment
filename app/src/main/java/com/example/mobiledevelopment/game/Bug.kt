package com.example.mobiledevelopment.game

data class Bug(
    val id: Int,
    val x: Float,
    val y: Float,
    val size: Float,
    val points: Int
) {
    val radius: Float
        get() = size / 2f

    fun contains(touchX: Float, touchY: Float): Boolean {
        val dx = touchX - x
        val dy = touchY - y

        return dx * dx + dy * dy <= radius * radius
    }
}