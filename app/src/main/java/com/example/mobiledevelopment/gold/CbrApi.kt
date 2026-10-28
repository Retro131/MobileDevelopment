package com.example.mobiledevelopment.gold

import retrofit2.http.GET
import retrofit2.http.Query

interface CbrApi {

    @GET("scripts/xml_metall.asp")
    suspend fun getMetalPrices(
        @Query("date_req1") dateFrom: String,
        @Query("date_req2") dateTo: String
    ): String
}