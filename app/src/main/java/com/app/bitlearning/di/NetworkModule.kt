/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.di

import android.content.Context
import com.app.bitlearning.apis.CurrencyApi
import com.app.bitlearning.apis.WeatherApi
import com.app.bitlearning.sample.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_CURRENCY_URL = "https://api.exchangerate-api.com/"
    private const val BASE_WEATHER_URL = "https://api.open-meteo.com/v1/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(@ApplicationContext context: Context): Interceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Add any authentication headers here if needed
        // For now, just pass through the request
        chain.proceed(requestBuilder.build())
    }

    @Provides
    @Singleton
    fun provideCookieManager(): CookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        cookieManager: CookieManager,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            },
        )
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // weather
    @Provides
    @Singleton
    @Named("weatherRetrofit")
    fun provideWeatherRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_WEATHER_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideWeatherApi(
        @Named("weatherRetrofit") retrofit: Retrofit,
    ): WeatherApi = retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi): WeatherRepository = WeatherRepository(api)

    // Currency API
    @Provides
    @Named("currencyRetrofit")
    @Singleton
    fun provideCurrencyRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_CURRENCY_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideCurrencyApi(@Named("currencyRetrofit") retrofit: Retrofit): CurrencyApi = retrofit.create(CurrencyApi::class.java)
}
