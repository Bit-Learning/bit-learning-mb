/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.courses.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.model.CourseCategory
import com.app.bitlearning.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CoursesUiState(
    val courses: List<Course> = emptyList(),
    val selectedCategory: CourseCategory? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val filteredCourses: List<Course>
        get() = if (selectedCategory == null) courses else courses.filter { it.category == selectedCategory }
}

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState

    init {
        loadCourses()
    }

    fun refresh() {
        loadCourses()
    }

    fun setCategory(category: CourseCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    private fun loadCourses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            courseRepository.getCourses()
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            courses = data,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Đã xảy ra lỗi khi tải khóa học",
                        )
                    }
                }
        }
    }
}
