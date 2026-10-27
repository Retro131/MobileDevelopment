package com.example.mobiledevelopment.gold

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class GoldQuoteStorage(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            "gold_quote",
            Context.MODE_PRIVATE
        )

    suspend fun save(quote: GoldQuote) {
        withContext(Dispatchers.IO) {
            val saved = preferences.edit()
                .putString("price", quote.price.toPlainString())
                .putString("date", quote.date.toString())
                .commit()

            check(saved) {
                "Не удалось сохранить цену золота"
            }
        }
    }

    suspend fun read(): GoldQuote? {
        return withContext(Dispatchers.IO) {
            val price = preferences.getString("price", null)
            val date = preferences.getString("date", null)

            if (price == null || date == null) {
                return@withContext null
            }

            try {
                GoldQuote(
                    price = price.toBigDecimal(),
                    date = LocalDate.parse(date)
                ).takeIf { it.price.signum() > 0 }
            } catch (e: IllegalArgumentException) {
                null
            } catch (e: java.time.format.DateTimeParseException) {
                null
            }
        }
    }
}