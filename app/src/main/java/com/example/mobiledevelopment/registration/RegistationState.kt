package com.example.mobiledevelopment.registration

import com.example.mobiledevelopment.model.Difficulty
import com.example.mobiledevelopment.model.Gender
import com.example.mobiledevelopment.model.Player
import java.io.Serializable
import java.time.LocalDate

data class RegistrationState(
    val fullName: String,
    val gender: Gender,
    val course: Int,
    val birthDate: LocalDate
) : Serializable {
    val isValid: Boolean
        get() = fullName.isNotBlank()
    fun toPlayer(difficulty: Difficulty): Player {
        require(isValid)
        return Player(
            fullName = fullName.trim(),
            gender = gender,
            course = course,
            difficulty = difficulty,
            birthDate = birthDate
        )
    }
    companion object {
        fun initial(config: RegistrationConfig) = RegistrationState(
            fullName = "",
            gender = Gender.Male,
            course = config.courses.first,
            birthDate = config.initialBirthDate
        )
    }
}