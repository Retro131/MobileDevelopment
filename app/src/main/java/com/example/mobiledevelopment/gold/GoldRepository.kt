package com.example.mobiledevelopment.gold

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GoldRepository(
    private val storage: GoldQuoteStorage,
    private val api: CbrApi = CbrClient.api
) {

    suspend fun getCached(): GoldQuote? {
        return storage.read()
    }

    suspend fun loadLatest(): GoldQuote {
        val today = LocalDate.now(
            ZoneId.of("Europe/Moscow")
        )

        val formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val xml = api.getMetalPrices(
            dateFrom = today.minusDays(30).format(formatter),
            dateTo = today.format(formatter)
        )

        val quote = withContext(Dispatchers.Default) {
            GoldXmlParser.parseLatest(xml)
        }

        storage.save(quote)

        return quote
    }
}