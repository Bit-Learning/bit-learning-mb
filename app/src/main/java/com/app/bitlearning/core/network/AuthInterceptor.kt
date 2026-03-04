/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.network

import com.app.bitlearning.core.preferences.AppPreferences
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that attaches the stored JWT access-token as an
 * `Authorization: Bearer <token>` header on every outgoing request.
 *
 * The token is read **synchronously** via [AppPreferences.getAccessTokenBlocking],
 * which is safe here because OkHttp dispatches requests on its own I/O thread-pool,
 * never on the main thread.
 */
class AuthInterceptor
@Inject
constructor(
    private val prefs: AppPreferences,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getAccessTokenBlocking()
        val request =
            if (!token.isNullOrBlank()) {
                chain
                    .request()
                    .newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
        return chain.proceed(request)
    }
}
