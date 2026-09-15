package com.example.mobiledevelopment.model

import java.io.Serializable
import java.time.LocalDate

enum class Gender(val title: String){
    Male("Мужской"),
    Female("Женский")
}
data class Player(
    val fullName: String,
    val gender: Gender,
    val course: Int,
    val difficulty: Int,
    val birthDate: LocalDate) : Serializable {
    val zodiac: Zodiac
        get() = Zodiac.fromDate(birthDate)
}