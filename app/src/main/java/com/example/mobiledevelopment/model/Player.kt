package com.example.mobiledevelopment.model

import java.io.Serializable
import java.time.LocalDate

enum class Gender(val title: String){
    Male("Мужской"),
    Female("Женский")
}
data class Player(
    val fullName: String,
    val gender: Gender? = null,
    val course: Int? = null,
    val birthDate: LocalDate? = null,
    val isGuest: Boolean = false,
    val id: Long = 0L,
) : Serializable {

    val zodiac: Zodiac?
        get() = birthDate?.let { Zodiac.fromDate(it) }
}