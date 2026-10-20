package com.example.mobiledevelopment.data

import com.example.mobiledevelopment.model.Gender
import com.example.mobiledevelopment.model.Player
import java.time.LocalDate

fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = id,
        fullName = fullName.trim(),
        gender = gender?.name,
        course = course,
        birthDate = birthDate?.toString(),
        isGuest = isGuest
    )
}

fun PlayerEntity.toPlayer(): Player {
    return Player(
        id = id,
        fullName = fullName,
        gender = gender?.let { Gender.valueOf(it) },
        course = course,
        birthDate = birthDate?.let { LocalDate.parse(it) },
        isGuest = isGuest
    )
}