package com.example.mobiledevelopment.model

import java.io.Serializable

enum class Difficulty {
    Easy,
    Medium,
    Hard,
    Custom
}

data class GameSettings(
    val difficulty: Difficulty = Difficulty.Easy,
    val speed: Int = 1,
    val maxCockroaches: Int = 10,
    val bonusInterval: Int = 10,
    val roundDuration: Int = 60
) : Serializable {

    fun withDifficulty(difficulty: Difficulty): GameSettings {
        return when (difficulty) {
            Difficulty.Easy -> copy(
                difficulty = difficulty,
                speed = 1,
                maxCockroaches = 25,
                bonusInterval = 10,
                roundDuration = 60
            )

            Difficulty.Medium -> copy(
                difficulty = difficulty,
                speed = 2,
                maxCockroaches = 15,
                bonusInterval = 15,
                roundDuration = 60
            )

            Difficulty.Hard -> copy(
                difficulty = difficulty,
                speed = 3,
                maxCockroaches = 10,
                bonusInterval = 30,
                roundDuration = 60
            )

            Difficulty.Custom -> copy(
                difficulty = difficulty
            )
        }
    }
}

object GameSettingsLimits {
    val speed = 1..5
    val maxCockroaches = 1..30
    val bonusInterval = 5..60
    val roundDuration = 30..180
}