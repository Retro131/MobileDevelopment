package com.example.mobiledevelopment.data

import com.example.mobiledevelopment.model.Difficulty
import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.model.Gender
import com.example.mobiledevelopment.model.Player
import java.time.LocalDate

fun Player.toEntity(settings: GameSettings): PlayerEntity {
    return PlayerEntity(
        id = id,
        fullName = fullName.trim(),
        gender = gender.name,
        course = course,
        birthDate = birthDate.toString(),
        difficulty = settings.difficulty.name,
        speed = settings.speed,
        maxCockroaches = settings.maxCockroaches,
        bonusInterval = settings.bonusInterval,
        roundDuration = settings.roundDuration
    )
}

fun PlayerEntity.toPlayer(): Player {
    return Player(
        id = id,
        fullName = fullName,
        gender = Gender.valueOf(gender),
        course = course,
        birthDate = LocalDate.parse(birthDate),
        difficulty = Difficulty.valueOf(difficulty)
    )
}

fun PlayerEntity.toSettings(): GameSettings {
    return GameSettings(
        difficulty = Difficulty.valueOf(difficulty),
        speed = speed,
        maxCockroaches = maxCockroaches,
        bonusInterval = bonusInterval,
        roundDuration = roundDuration
    )
}