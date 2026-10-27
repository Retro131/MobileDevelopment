package com.example.mobiledevelopment.gold

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GoldRepository(
    private val api: CbrApi = CbrClient.api
) {

    suspend fun loadLatest(): GoldQuote {
        val today = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val xml = api.getMetalPrices(
            dateFrom = today.minusDays(5).format(formatter),
            dateTo = today.format(formatter)
        )

        return withContext(Dispatchers.Default) {
            GoldXmlParser.parseLatest(xml)
        }
    }
}