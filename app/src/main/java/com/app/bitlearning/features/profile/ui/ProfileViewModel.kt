/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.data.repository.toUserMessage
import com.app.bitlearning.domain.model.Certificate
import com.app.bitlearning.domain.model.User
import com.app.bitlearning.domain.repository.AuthRepository
import com.app.bitlearning.domain.repository.CourseRepository
import com.app.bitlearning.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val certificates: List<Certificate> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val user = userRepository.getUserProfile().getOrNull()
            val completedCountResult = courseRepository.getEnrolledCourses(page = 0, size = 100)
            _uiState.update {
                it.copy(
                    user = user,
                    certificates = completedCountResult
                        .getOrDefault(emptyList())
                        .filter { course -> course.isCompleted || course.progress >= 1f }
                        .map { course ->
                            Certificate(
                                id = "course-${course.id}",
                                courseId = course.id,
                                courseTitle = course.title,
                                issuedDate = "",
                                thumbnailUrl = course.thumbnailUrl,
                            )
                        },
                    isLoading = false,
                    errorMessage = completedCountResult.exceptionOrNull()?.toUserMessage("Không thể tải dữ liệu hồ sơ"),
                )
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }
}
