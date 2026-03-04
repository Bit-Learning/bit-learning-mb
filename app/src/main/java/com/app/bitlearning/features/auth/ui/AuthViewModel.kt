/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.core.log.MainLog
import com.app.bitlearning.core.preferences.AppPreferences
import com.app.bitlearning.domain.model.LoginRequest
import com.app.bitlearning.domain.model.RegisterRequest
import com.app.bitlearning.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
//  State
// ─────────────────────────────────────────────

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginMode: Boolean = true,
    val authSuccess: Boolean = false,
    /** true → local mock data; false → real API at [NetworkModule.BASE_URL] */
    val useMock: Boolean = true,
    /**
     * Set to true in real mode to signal [AuthScreen] to launch the Google
     * Sign-In SDK intent. Cleared automatically after the intent is launched.
     */
    val pendingGoogleSignIn: Boolean = false,
    /**
     * Set after a successful *real* registration to prompt the user to check
     * their email (backend sends an activation email before login is allowed).
     */
    val registerMessage: String? = null,
)

// ─────────────────────────────────────────────
//  ViewModel
// ─────────────────────────────────────────────

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val prefs: AppPreferences,
        private val log: MainLog,
    ) : ViewModel() {

        companion object {
            private const val TAG = "AuthViewModel"
        }

        private val _uiState = MutableStateFlow(AuthUiState())
        val uiState: StateFlow<AuthUiState> = _uiState

        init {
            // Keep [AuthUiState.useMock] in sync with persisted preference.
            viewModelScope.launch {
                prefs.useMock.collect { mock ->
                    _uiState.update { it.copy(useMock = mock) }
                }
            }
        }

        // ── Field updates ────────────────────────────────────────────────────

        fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }

        fun onPasswordChange(value: String) =
            _uiState.update { it.copy(password = value, error = null) }

        fun onNameChange(value: String) = _uiState.update { it.copy(name = value, error = null) }

        fun switchMode() =
            _uiState.update {
                it.copy(isLoginMode = !it.isLoginMode, error = null, registerMessage = null)
            }

        // ── Mock / API toggle ─────────────────────────────────────────────────

        /** Persist the toggle and immediately reflect it in the UI state. */
        fun toggleMock() {
            viewModelScope.launch { prefs.setUseMock(!_uiState.value.useMock) }
        }

        // ── Email / Password auth ─────────────────────────────────────────────

        fun login() {
            val state = _uiState.value
            if (state.email.isBlank() || state.password.isBlank()) {
                _uiState.update { it.copy(error = "Vui lòng nhập email và mật khẩu") }
                return
            }
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                authRepository
                    .login(LoginRequest(state.email, state.password))
                    .onSuccess { _uiState.update { it.copy(isLoading = false, authSuccess = true) } }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
            }
        }

        fun register() {
            val state = _uiState.value
            if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank()) {
                _uiState.update { it.copy(error = "Vui lòng điền đầy đủ thông tin") }
                return
            }
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                authRepository
                    .register(RegisterRequest(state.name, state.email, state.password))
                    .onSuccess {
                        if (state.useMock) {
                            // Mock auto-logs in after registration.
                            _uiState.update { it.copy(isLoading = false, authSuccess = true) }
                        } else {
                            // Real API: user must activate email before logging in.
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    registerMessage =
                                        "Đăng ký thành công!\n" +
                                            "Hãy kiểm tra email để kích hoạt tài khoản trước khi đăng nhập.",
                                )
                            }
                        }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
            }
        }

        // ── Google Sign-In (SDK – ID Token flow) ───────────────────────────────

        /**
         * Start Google Sign-In.
         * - **Mock**: directly resolves with a fake ID token.
         * - **Real**: sets [AuthUiState.pendingGoogleSignIn] = true; the screen
         *   launches the Google Sign-In SDK intent and calls back [onGoogleIdToken].
         */
        fun initiateGoogleLogin() {
            log.d(TAG, "initiateGoogleLogin() — useMock=${_uiState.value.useMock}")
            if (_uiState.value.useMock) {
                onGoogleIdToken("mock_google_id_token_${System.currentTimeMillis()}")
            } else {
                log.d(TAG, "Setting pendingGoogleSignIn=true — chờ Android SDK launcher")
                _uiState.update { it.copy(pendingGoogleSignIn = true, error = null) }
            }
        }

        /** Call after the Google Sign-In intent has been launched to clear the flag. */
        fun clearGoogleSignInPending() = _uiState.update { it.copy(pendingGoogleSignIn = false) }

        /**
         * Called by [AuthScreen] (or mock) once the Google ID token is available.
         * Posts the token to the backend for verification and JWT exchange.
         *
         * @param idToken the raw JWT from [GoogleSignInAccount.getIdToken()].
         */
        fun onGoogleIdToken(idToken: String) {
            log.d(TAG, "onGoogleIdToken() — nhận được ID token, độ dài=${idToken.length} ký tự")
            log.d(TAG, "ID token (20 ký tự đầu): ${idToken.take(20)}...")
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null, pendingGoogleSignIn = false) }
                log.d(TAG, "Gọi authRepository.loginWithGoogle(idToken)...")
                authRepository.loginWithGoogle(idToken)
                    .onSuccess { token ->
                        log.i(TAG, "loginWithGoogle SUCCESS — accessToken nhận được")
                        _uiState.update { it.copy(isLoading = false, authSuccess = true) }
                    }
                    .onFailure { e ->
                        log.e(TAG, "loginWithGoogle FAILED — ${e::class.simpleName}: ${e.message}")
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
            }
        }

        /**
         * GitHub login.
         * - **Mock**: resolves with a fake token.
         * - **Real**: shows an error (SDK-based GitHub Sign-In is not yet supported).
         */
        fun initiateGitHubLogin() {
            if (_uiState.value.useMock) {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                    authRepository.loginWithGitHub("mock_github_code")
                        .onSuccess { _uiState.update { it.copy(isLoading = false, authSuccess = true) } }
                        .onFailure { e ->
                            _uiState.update { it.copy(isLoading = false, error = e.message) }
                        }
                }
            } else {
                _uiState.update {
                    it.copy(error = "GitHub login chưa khả dụng trên mobile. Vui lòng dùng email/mật khẩu.")
                }
            }
        }

        fun onOAuth2Error(message: String) {
            log.e(TAG, "onOAuth2Error() — $message")
            _uiState.update { it.copy(isLoading = false, error = message, pendingGoogleSignIn = false) }
        }
    }
