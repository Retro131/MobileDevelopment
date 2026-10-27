package com.example.mobiledevelopment.gold

import java.math.BigDecimal
import java.math.RoundingMode

object GoldPointsCalculator {

    private val pricePerPoint = BigDecimal("100")

    fun calculate(quote: GoldQuote): Int {
        require(quote.price.signum() > 0) {
            "Цена золота должна быть положительной"
        }

        return quote.price
            .divide(
                pricePerPoint,
                0,
                RoundingMode.HALF_UP
            )
            .intValueExact()
            .coerceAtLeast(1)
    }
}