package com.example.mobiledevelopment.game

import com.example.mobiledevelopment.game.bonuses.Bonus
import com.example.mobiledevelopment.model.GameSettings

data class GameState(
    val playerId: Long,
    val settings: GameSettings,

    val bugs: List<Bug> = emptyList(),

    val score: Int = 0,
    val hits: Int = 0,
    val misses: Int = 0,

    val remainingSeconds: Int = settings.roundDuration,
    val finished: Boolean = false,

    val goldLoading: Boolean = true,
    val goldPoints: Int? = null,
    val goldError: String? = null,

    val fieldBonus: Bonus? = null,
    val activeBonus: Bonus? = null,
    val bonusRemaining: Float = 0f,

    val isSaving: Boolean = false,
    val resultSaved: Boolean = false,
    val saveError: String? = null
)