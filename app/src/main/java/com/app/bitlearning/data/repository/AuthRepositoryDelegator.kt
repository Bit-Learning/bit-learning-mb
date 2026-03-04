/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.data.repository

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
import kotlinx.coroutines.flow.first

/**
 * Delegates all [AuthRepository] calls to either [MockAuthRepositoryImpl] or
 * [RealAuthRepositoryImpl] depending on [AppPreferences.useMock].
 *
 * Switch between local mock data and the real backend at runtime via the
 * "Mock / API" chip in the auth dialog.
 */
@Singleton
class AuthRepositoryDelegator
    @Inject
    constructor(
        private val mock: MockAuthRepositoryImpl,
        private val real: RealAuthRepositoryImpl,
        private val prefs: AppPreferences,
    ) : AuthRepository {

        private val _isLoggedIn = MutableStateFlow(false)

        private suspend fun delegate(): AuthRepository =
            if (prefs.useMock.first()) mock else real

        override fun isLoggedIn(): Flow<Boolean> = _isLoggedIn

        override suspend fun login(request: LoginRequest): Result<AuthToken> =
            delegate().login(request).also { if (it.isSuccess) _isLoggedIn.value = true }

        override suspend fun register(request: RegisterRequest): Result<Unit> =
            delegate().register(request)

        override suspend fun loginWithGoogle(code: String): Result<AuthToken> =
            delegate().loginWithGoogle(code).also { if (it.isSuccess) _isLoggedIn.value = true }

        override suspend fun loginWithGitHub(code: String): Result<AuthToken> =
            delegate().loginWithGitHub(code).also { if (it.isSuccess) _isLoggedIn.value = true }

        override suspend fun logout() {
            delegate().logout()
            _isLoggedIn.value = false
        }

        override suspend fun getCurrentUser(): User? = delegate().getCurrentUser()
    }
