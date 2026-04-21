/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HistoryFilter(val label: String) {
    ALL("Tất cả"),
    IN_PROGRESS("Đang học"),
    COMPLETED("Đã hoàn thành"),
}

data class LearningHistoryUiState(
    val courses: List<Course> = emptyList(),
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
) {
    val displayed: List<Course>
        get() {
            val byFilter = when (selectedFilter) {
                HistoryFilter.ALL -> courses
                HistoryFilter.IN_PROGRESS -> courses.filter { it.progress in 0.01f..0.99f }
                HistoryFilter.COMPLETED -> courses.filter { it.progress >= 1f }
            }
            return if (searchQuery.isBlank()) {
                byFilter
            } else {
                byFilter.filter { it.title.contains(searchQuery, ignoreCase = true) }
            }
        }
}

@OptIn(FlowPreview::class)
@HiltViewModel
class LearningHistoryViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningHistoryUiState())
    val uiState: StateFlow<LearningHistoryUiState> = _uiState

    private val queryFlow = MutableStateFlow("")

    init {
        loadHistory()
        viewModelScope.launch {
            queryFlow
                .debounce(250)
                .distinctUntilChanged()
                .collect { q -> _uiState.update { it.copy(searchQuery = q) } }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            courseRepository.getMyCourses()
                .onSuccess { data -> _uiState.update { it.copy(courses = data, isLoading = false) } }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    fun setFilter(filter: HistoryFilter) = _uiState.update { it.copy(selectedFilter = filter) }

    fun onQueryChange(q: String) {
        _uiState.update { it.copy(searchQuery = q) }
        queryFlow.value = q
    }
}
