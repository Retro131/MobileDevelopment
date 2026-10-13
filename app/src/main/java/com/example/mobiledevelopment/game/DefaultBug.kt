package com.example.mobiledevelopment.game

import com.example.mobiledevelopment.R

data class DefaultBug(
    override val id: Int,
    override val x: Float,
    override val y: Float
) : Bug() {

    override val size: Float = 140f

    override val points: Int = 10

    override val imageRes: Int = R.drawable.bug
}