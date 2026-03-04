/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Persistent app preferences backed by Jetpack DataStore.
 *
 * Keys:
 *  - [USE_MOCK_KEY]    – if true, the app uses local mock data instead of real API calls.
 *                        Defaults to **true** so devs can run the app without a backend.
 *  - [ACCESS_TOKEN_KEY] – JWT access token stored after a successful login.
 */
@Singleton
class AppPreferences
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        // Build the DataStore without the property-delegate shortcut so it can live
        // inside a class rather than being a top-level property.
        private val dataStore: DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile("app_prefs") },
            )

        // ── Flows ── //

        val useMock: Flow<Boolean> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[USE_MOCK_KEY] ?: false }

        val accessToken: Flow<String?> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[ACCESS_TOKEN_KEY] }

        // ── Writers ── //

        suspend fun setUseMock(value: Boolean) {
            dataStore.edit { it[USE_MOCK_KEY] = value }
        }

        suspend fun saveAccessToken(token: String) {
            dataStore.edit { it[ACCESS_TOKEN_KEY] = token }
        }

        suspend fun clearTokens() {
            dataStore.edit {
                it.remove(ACCESS_TOKEN_KEY)
            }
        }

        /**
         * Blocking read for use inside OkHttp Interceptor (runs on I/O thread, not main).
         * Do **not** call from the main thread.
         */
        fun getAccessTokenBlocking(): String? = runBlocking { accessToken.first() }

        // ── Keys ── //

        companion object {
            private val USE_MOCK_KEY = booleanPreferencesKey("use_mock")
            private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        }
    }
