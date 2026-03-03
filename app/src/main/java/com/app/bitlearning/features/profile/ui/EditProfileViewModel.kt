/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.domain.model.User
import com.app.bitlearning.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val birthDate: String = "",
    val bio: String = "",
    val avatarUrl: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val user = userRepository.getUserProfile().getOrNull()
            _uiState.update {
                it.copy(
                    name = user?.name ?: "",
                    email = user?.email ?: "",
                    phone = "",
                    birthDate = "",
                    bio = "",
                    avatarUrl = user?.avatarUrl,
                    isLoading = false,
                )
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onBirthDateChange(value: String) = _uiState.update { it.copy(birthDate = value) }
    fun onBioChange(value: String) = _uiState.update { it.copy(bio = value) }

    fun saveProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val current = _uiState.value
            val updatedUser = User(
                id = "",
                name = current.name,
                email = current.email,
                avatarUrl = current.avatarUrl,
                memberSince = 2024,
                isPremium = false,
            )
            val result = userRepository.updateProfile(updatedUser)
            if (result.isSuccess) {
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Lưu thất bại",
                    )
                }
            }
        }
    }
}
