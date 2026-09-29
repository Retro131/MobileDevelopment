package com.example.mobiledevelopment.model
import java.io.Serializable

data class GameSettings(
    val speed: Int = 1,
    val maxCockroaches: Int = 10,
    val bonusInterval: Int = 10,
    val roundDuration: Int = 60
) : Serializable

object GameSettingsLimits {
    val speed = 1..5
    val maxCockroaches = 1..30
    val bonusInterval = 5..60
    val roundDuration = 30..180
}