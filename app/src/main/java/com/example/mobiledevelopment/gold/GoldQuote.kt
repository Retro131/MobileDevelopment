package com.example.mobiledevelopment.gold

import java.math.BigDecimal
import java.time.LocalDate

data class GoldQuote(
    val price: BigDecimal,
    val date: LocalDate
)