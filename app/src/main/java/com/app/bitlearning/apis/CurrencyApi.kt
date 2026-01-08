/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.apis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class ExchangeRateResponse(
    val provider: String,
    val base: String,
    val date: String,
    val time_last_updated: Long,
    val rates: Map<String, Double>,
)

interface CurrencyApi {
    @GET("v4/latest/{base}")
    suspend fun getExchangeRates(
        @Path("base") base: String,
    ): Response<ExchangeRateResponse>
}
