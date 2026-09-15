package com.example.mobiledevelopment.registration

import java.time.LocalDate

data class RegistrationConfig(
    val courses: IntRange = 1..4,
    val difficulties: IntRange = 1..5,
    val earliestBirthDate: LocalDate = LocalDate.of(1900, 1, 1),
    val initialBirthDate: LocalDate = LocalDate.now()
) {
    init {
        require(!courses.isEmpty())
        require(difficulties.first < difficulties.last)
        require(!earliestBirthDate.isAfter(initialBirthDate))
        require(!initialBirthDate.isAfter(LocalDate.now()))
    }
}