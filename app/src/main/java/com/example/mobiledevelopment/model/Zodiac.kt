package com.example.mobiledevelopment.model

import java.time.LocalDate
import java.time.MonthDay

enum class Zodiac(
    val title: String,
    val symbol: String,
    val start: MonthDay
) {
    AQUARIUS("Водолей", "♒", MonthDay.of(1, 20)),
    PISCES("Рыбы", "♓", MonthDay.of(2, 19)),
    ARIES("Овен", "♈", MonthDay.of(3, 21)),
    TAURUS("Телец", "♉", MonthDay.of(4, 20)),
    GEMINI("Близнецы", "♊", MonthDay.of(5, 21)),
    CANCER("Рак", "♋", MonthDay.of(6, 22)),
    LEO("Лев", "♌", MonthDay.of(7, 23)),
    VIRGO("Дева", "♍", MonthDay.of(8, 23)),
    LIBRA("Весы", "♎", MonthDay.of(9, 23)),
    SCORPIO("Скорпион", "♏", MonthDay.of(10, 23)),
    SAGITTARIUS("Стрелец", "♐", MonthDay.of(11, 22)),
    CAPRICORN("Козерог", "♑", MonthDay.of(12, 22));
    companion object {
        private val ordered = entries.sortedBy { it.start }
        fun fromDate(date: LocalDate): Zodiac {
            val day = MonthDay.from(date)
            return ordered.lastOrNull { day >= it.start } ?: CAPRICORN
        }
    }
}