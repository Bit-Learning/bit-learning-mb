/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.data.repository

import com.app.bitlearning.core.log.MainLog
import com.app.bitlearning.core.network.BitLearningApiService
import com.app.bitlearning.core.network.GoogleIdTokenBody
import com.app.bitlearning.core.network.LoginBody
import com.app.bitlearning.core.network.OAuth2LoginBody
import com.app.bitlearning.core.network.RegisterBody
import com.app.bitlearning.core.preferences.AppPreferences
import com.app.bitlearning.domain.model.AuthToken
import com.app.bitlearning.domain.model.LoginRequest
import com.app.bitlearning.domain.model.RegisterRequest
import com.app.bitlearning.domain.model.User
import com.app.bitlearning.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * [AuthRepository] implementation that calls the real Bit Learning backend.
 *
 * Base URL: [com.app.bitlearning.core.network.NetworkModule.BASE_URL]
 *
 * OAuth2 notes
 * ────────────
 * [loginWithGoogle] / [loginWithGitHub] receive the authorization *code* obtained
 * via the AppAuth browser flow and exchange it for JWT tokens through the backend.
 *
 * For end-to-end OAuth2 to work the following must be configured:
 *  1. Register `com.app.bitlearning://oauth2redirect` as an Authorized Redirect URI
 *     in the Google Cloud Console / GitHub OAuth App settings.
 *  2. Set `oauth2.google.redirect-uri` (and github) on the backend to the same value.
 */
@Singleton
class RealAuthRepositoryImpl
@Inject
constructor(
    private val api: BitLearningApiService,
    private val prefs: AppPreferences,
    private val log: MainLog,
) : AuthRepository {

    companion object {
        private const val TAG = "RealAuthRepo"
    }

    private val isLoggedIn = MutableStateFlow(false)

    override fun isLoggedIn(): Flow<Boolean> = isLoggedIn

    // ── Email / Password ────────────────────────────────────────────────

    override suspend fun login(request: LoginRequest): Result<AuthToken> = runCatching {
        val wrapper = api.login(LoginBody(request.email, request.password))
        val data =
            wrapper.data
                ?: error(wrapper.message ?: "Đăng nhập thất bại")

        val accessToken =
            data.accessToken ?: error("Phản hồi từ máy chủ không hợp lệ")

        prefs.saveAccessToken(accessToken)
        isLoggedIn.value = true

        // Refresh token is managed via HttpOnly cookie by OkHttp's CookieJar.
        AuthToken(
            accessToken = accessToken,
            refreshToken = "",
            expiresAt = 0L,
        )
    }

    /**
     * Registers a new user account.
     *
     * The backend sends an activation email — the user must click the link
     * before they can log in. Returns [Result.success] on HTTP 2xx.
     */
    override suspend fun register(request: RegisterRequest): Result<Unit> = runCatching {
        // Split "Nguyễn Văn A" → firstName="Nguyễn Văn", lastName="A"
        val parts = request.name.trim().split(" ")
        val firstName = if (parts.size >= 2) parts.dropLast(1).joinToString(" ") else request.name
        val lastName = if (parts.size >= 2) parts.last() else request.name

        val wrapper =
            api.register(
                RegisterBody(
                    firstName = firstName,
                    lastName = lastName,
                    email = request.email,
                    password = request.password,
                ),
            )

        if (wrapper.status in 200..299) {
            Unit
        } else {
            error(wrapper.message ?: "Đăng ký thất bại")
        }
    }

    // ── OAuth2 ──────────────────────────────────────────────────────────

    /**
     * Google Sign-In (ID Token flow).
     * [idToken] comes from [GoogleSignInAccount.getIdToken()] on the device.
     * The backend verifies it with Google and returns system JWTs.
     */
    override suspend fun loginWithGoogle(idToken: String): Result<AuthToken> = runCatching {
        log.d(TAG, "POST /auth/oauth2/google/mobile — gửi ID token (${idToken.length} ký tự)")
        val wrapper = api.loginWithGoogleIdToken(GoogleIdTokenBody(idToken))
        log.d(TAG, "Response status=${wrapper.status} message=${wrapper.message} data=${wrapper.data != null}")
        if (wrapper.data == null) {
            log.e(TAG, "Backend trả về null data — message: ${wrapper.message}")
        }
        handleOAuth2Response(wrapper.data, wrapper.message)
    }.also { result ->
        result.onFailure { e ->
            log.e(TAG, "loginWithGoogle exception: ${e::class.simpleName}: ${e.message}")
        }
    }

    override suspend fun loginWithGitHub(code: String): Result<AuthToken> = runCatching {
        // GitHub browser flow (not active on mobile yet – see initiateGitHubLogin).
        // The redirect URI must match what was registered in the GitHub OAuth App.
        val wrapper =
            api.loginWithGitHub(
                OAuth2LoginBody(code = code, redirectUri = "https://bit-learning.lch.id.vn/auth/github/callback"),
            )
        handleOAuth2Response(wrapper.data, wrapper.message)
    }

    // ── Session ─────────────────────────────────────────────────────────

    override suspend fun logout() {
        runCatching { api.logout() }
        prefs.clearTokens()
        isLoggedIn.value = false
    }

    override suspend fun getCurrentUser(): User? = null

    // ── Helpers ─────────────────────────────────────────────────────────

    private suspend fun handleOAuth2Response(
        data: com.app.bitlearning.core.network.OAuth2ApiResponse?,
        fallbackMessage: String?,
    ): AuthToken {
        val token =
            data?.accessToken ?: error(fallbackMessage ?: "OAuth2 đăng nhập thất bại")

        prefs.saveAccessToken(token)
        isLoggedIn.value = true

        return AuthToken(
            accessToken = token,
            refreshToken = data.refreshToken ?: "",
            expiresAt = 0L,
        )
    }
}
