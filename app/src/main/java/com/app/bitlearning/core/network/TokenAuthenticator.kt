/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.network

import com.app.bitlearning.core.log.MainLog
import com.app.bitlearning.core.preferences.AppPreferences
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

@Singleton
class TokenAuthenticator
@Inject
constructor(
    private val prefs: AppPreferences,
    private val refreshApi: BitLearningRefreshApiService,
    private val log: MainLog,
) : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
        private const val RETRY_LIMIT = 2
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val requestPath = response.request.url.encodedPath
        if (requestPath.endsWith("/auth/refresh-token") || responseCount(response) >= RETRY_LIMIT) {
            return null
        }

        val authHeader = response.request.header("Authorization") ?: return null
        val requestToken = authHeader.removePrefix("Bearer ").trim()
        if (requestToken.isBlank()) {
            return null
        }

        synchronized(this) {
            val latestToken = prefs.getAccessTokenBlocking()
            if (!latestToken.isNullOrBlank() && latestToken != requestToken) {
                log.d(TAG, "Retrying 401 request with a newer access token from storage")
                return response.request
                    .newBuilder()
                    .header("Authorization", "Bearer $latestToken")
                    .build()
            }

            return try {
                log.i(TAG, "Received 401 for $requestPath, attempting refresh-token flow")
                val refreshResponse = refreshApi.refreshToken().execute()
                if (!refreshResponse.isSuccessful) {
                    log.e(TAG, "Refresh-token request failed with HTTP ${refreshResponse.code()}")
                    prefs.clearTokensBlocking()
                    null
                } else {
                    val newToken = refreshResponse.body()?.data?.accessToken
                    if (newToken.isNullOrBlank()) {
                        log.e(TAG, "Refresh-token response did not contain a new access token")
                        prefs.clearTokensBlocking()
                        null
                    } else {
                        prefs.saveAccessTokenBlocking(newToken)
                        log.i(TAG, "Refresh-token flow succeeded, retrying original request")
                        response.request
                            .newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .build()
                    }
                }
            } catch (e: Exception) {
                log.e(TAG, "Refresh-token flow failed: ${e.message}")
                prefs.clearTokensBlocking()
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
