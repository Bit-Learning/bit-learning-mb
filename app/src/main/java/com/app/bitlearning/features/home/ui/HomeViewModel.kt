/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.model.User
import com.app.bitlearning.domain.repository.AuthRepository
import com.app.bitlearning.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: User? = null,
    val enrolledCourses: List<Course> = emptyList(),
    val recommendedCourses: List<Course> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = authRepository.getCurrentUser()
            val enrolled = courseRepository.getMyCourses().getOrElse { emptyList() }
            val recommended = courseRepository.getCourses().getOrElse { emptyList() }
            _uiState.update {
                it.copy(
                    user = user,
                    enrolledCourses = enrolled,
                    recommendedCourses = recommended,
                    isLoading = false,
                )
            }
        }
    }

    fun refresh() = loadData()
}
