/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.coursedetail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.model.Lecture
import com.app.bitlearning.features.coursedetail.domain.GetCourseDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CourseDetailUiState(
    val course: Course? = null,
    val lessons: List<Lecture> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val getCourseDetailUseCase: GetCourseDetailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val courseId: Int = savedStateHandle.get<String>("courseId")?.toIntOrNull() ?: 0
    private val _uiState = MutableStateFlow(CourseDetailUiState())
    val uiState: StateFlow<CourseDetailUiState> = _uiState

    init {
        loadCourseDetail()
    }

    private fun loadCourseDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getCourseDetailUseCase(courseId)
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            course = data.course,
                            lessons = data.lessons,
                            isLoading = false,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
