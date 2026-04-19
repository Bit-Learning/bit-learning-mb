/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.network

import com.app.bitlearning.BuildConfig
import com.app.bitlearning.core.preferences.AppPreferences
import com.app.bitlearning.data.repository.*
import com.app.bitlearning.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Real backend URL.
     *
     * For Android Emulator the host machine is accessible at 10.0.2.2.
     * For a physical device on the same network use the machine's LAN IP (e.g. 10.0.0.2).
     */
    const val BASE_URL = "https://bit-api-dev.lch.id.vn/api/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(prefs: AppPreferences): AuthInterceptor = AuthInterceptor(prefs)

    @Provides
    @Singleton
    fun provideCookieJar(cookieJar: PersistentCookieJar): CookieJar = cookieJar

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshOkHttpClient(
        cookieJar: CookieJar,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        // This client relies only on the refresh_token cookie and must not send stale bearer tokens.
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshRetrofit(@Named("refresh") client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideRefreshApiService(@Named("refresh") retrofit: Retrofit): BitLearningRefreshApiService =
        retrofit.create(BitLearningRefreshApiService::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        cookieJar: CookieJar,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): BitLearningApiService = retrofit.create(BitLearningApiService::class.java)
}
