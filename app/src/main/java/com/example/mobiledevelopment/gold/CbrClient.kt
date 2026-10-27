package com.example.mobiledevelopment.gold

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object CbrClient {

    val api: CbrApi = Retrofit.Builder()
        .baseUrl("https://www.cbr.ru/")
        .addConverterFactory(
            ScalarsConverterFactory.create()
        )
        .build()
        .create(CbrApi::class.java)
}