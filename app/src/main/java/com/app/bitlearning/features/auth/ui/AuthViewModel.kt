/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.auth.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.core.log.MainLog
import com.app.bitlearning.core.preferences.AppPreferences
import com.app.bitlearning.domain.model.LoginRequest
import com.app.bitlearning.domain.model.RegisterRequest
import com.app.bitlearning.domain.repository.AuthRepository
import com.app.bitlearning.features.auth.GoogleAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
    private val log: MainLog,
    private val googleAuthManager: GoogleAuthManager,
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    /**
     * One-shot event that carries the Google Sign-In [Intent] to be launched
     * by the UI layer.  Using a [SharedFlow] avoids storing UI-layer concerns
     * (Activity intents) in the persistent [AuthUiState].
     */
    private val _googleSignInEvent = MutableSharedFlow<Intent>(extraBufferCapacity = 1)
    val googleSignInEvent: SharedFlow<Intent> = _googleSignInEvent.asSharedFlow()

    // ── Field updates ────────────────────────────────────────────────────

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null) }

    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, error = null) }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, error = null) }

    fun switchMode() = _uiState.update {
        it.copy(isLoginMode = !it.isLoginMode, error = null, registerMessage = null)
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
                    _uiState.update { it.copy(isLoading = false, error = toAuthErrorMessage(e)) }
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
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = toAuthErrorMessage(e)) }
                }
        }
    }

    // ── Google Sign-In (SDK – ID Token flow) ───────────────────────────────

    /**
     * Start Google Sign-In.
     * Starts Google Sign-In by obtaining the sign-in [Intent] via [GoogleAuthManager].
     */
    fun initiateGoogleLogin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            googleAuthManager.getSignInIntent()
                .onSuccess { intent ->
                    _uiState.update { it.copy(isLoading = false) }
                    _googleSignInEvent.emit(intent)
                }
                .onFailure { e ->
                    log.e(TAG, "getSignInIntent failed: ${e.message}")
                    _uiState.update { it.copy(isLoading = false, error = toAuthErrorMessage(e)) }
                }
        }
    }

    /**
     * Called by the UI with the [Intent] returned from the Google Sign-In
     * activity result.  Delegates token extraction to [GoogleAuthManager].
     *
     * @param data The result [Intent]; pass `null` if the user cancelled.
     */
    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            googleAuthManager.getIdTokenFromResult(data)
                .onSuccess { idToken -> onGoogleIdToken(idToken) }
                .onFailure { e ->
                    log.e(TAG, "handleGoogleSignInResult failed: ${e.message}")
                    _uiState.update { it.copy(isLoading = false, error = toAuthErrorMessage(e)) }
                }
        }
    }

    /**
     * Called once the Google ID token is available.
     * Posts the token to the backend for verification and JWT exchange.
     *
     * @param idToken the raw JWT from [GoogleSignInAccount.getIdToken()].
     */
    fun onGoogleIdToken(idToken: String) {
        log.d(TAG, "onGoogleIdToken() — nhận được ID token, độ dài=${idToken.length} ký tự")
        log.d(TAG, "ID token (20 ký tự đầu): ${idToken.take(20)}...")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            log.d(TAG, "Gọi authRepository.loginWithGoogle(idToken)...")
            authRepository.loginWithGoogle(idToken)
                .onSuccess {
                    log.i(TAG, "loginWithGoogle SUCCESS — accessToken nhận được")
                    _uiState.update { it.copy(isLoading = false, authSuccess = true) }
                }
                .onFailure { e ->
                    log.e(TAG, "loginWithGoogle FAILED — ${e::class.simpleName}: ${e.message}")
                    _uiState.update { it.copy(isLoading = false, error = toAuthErrorMessage(e)) }
                }
        }
    }

    /**
     * GitHub login.
     * GitHub login is not yet supported on mobile.
     */
    fun initiateGitHubLogin() {
        _uiState.update {
            it.copy(error = "GitHub login chưa khả dụng trên mobile. Vui lòng dùng email/mật khẩu.")
        }
    }

    fun onOAuth2Error(message: String) {
        log.e(TAG, "onOAuth2Error() — $message")
        _uiState.update { it.copy(isLoading = false, error = message) }
    }

    private fun toAuthErrorMessage(error: Throwable): String {
        return if (error is HttpException && error.code() == 401) {
            "Sai tài khoản hoặc mật khẩu"
        } else {
            error.message ?: "Đăng nhập thất bại"
        }
    }
}
