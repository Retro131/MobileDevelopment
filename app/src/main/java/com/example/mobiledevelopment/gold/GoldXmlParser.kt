package com.example.mobiledevelopment.gold

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object GoldXmlParser {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun parseLatest(xml: String): GoldQuote {
        val parser = Xml.newPullParser()
        parser.setInput(StringReader(xml))

        var latest: GoldQuote? = null
        var goldDate: LocalDate? = null

        var event = parser.eventType

        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "Record" -> {
                            val code = parser.getAttributeValue(
                                null,
                                "Code"
                            )

                            goldDate = if (code == "1") {
                                LocalDate.parse(
                                    parser.getAttributeValue(null, "Date"),
                                    dateFormatter
                                )
                            } else {
                                null
                            }
                        }

                        "Buy" -> {
                            val date = goldDate

                            if (date != null) {
                                val price = parser.nextText()
                                    .trim()
                                    .replace(" ", "")
                                    .replace(",", ".")
                                    .toBigDecimal()

                                require(price.signum() > 0) {
                                    "ЦБ вернул некорректную цену золота"
                                }

                                val quote = GoldQuote(
                                    price = price,
                                    date = date
                                )

                                val previous = latest

                                if (
                                    previous == null ||
                                    quote.date.isAfter(previous.date)
                                ) {
                                    latest = quote
                                }
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "Record") {
                        goldDate = null
                    }
                }
            }

            event = parser.next()
        }

        return latest ?: error("В ответе ЦБ не найдена цена золота")
    }
}