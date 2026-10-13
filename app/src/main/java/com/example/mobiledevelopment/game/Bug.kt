package com.example.mobiledevelopment.game

data class Bug(
    val id: Int,
    val x: Float,
    val y: Float,
    val size: Float,
    val points: Int
) {
    fun contains(touchX: Float, touchY: Float): Boolean {
        val dx = touchX - x
        val dy = touchY - y
        val radius = size / 2f

        return dx * dx + dy * dy <= radius * radius
    }
}