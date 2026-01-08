/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.apis

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("hourly") hourly: String = "temperature_2m,wind_speed_10m",
    ): HourlyWeatherResponse
}

data class HourlyWeatherResponse(
    val hourly: HourlyWeatherData,
)

data class HourlyWeatherData(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val wind_speed_10m: List<Double>,
)
