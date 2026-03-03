/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.search.ui

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

data class SearchUiState(
    val query: String = "",
    val results: List<Course> = emptyList(),
    val trendingCourses: List<Course> = emptyList(),
    val recentSearches: List<String> = listOf("Lập trình React Native", "Thiết kế UI/UX cơ bản"),
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class PopularCategory(
    val label: String,
    val icon: String,
)

val popularCategories = listOf(
    PopularCategory("Công nghệ", "terminal"),
    PopularCategory("Thiết kế", "palette"),
    PopularCategory("Kinh doanh", "payments"),
    PopularCategory("Ngoại ngữ", "language"),
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private val _queryFlow = MutableStateFlow("")

    init {
        loadTrending()
        viewModelScope.launch {
            _queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query -> executeSearch(query) }
        }
    }

    private fun loadTrending() {
        viewModelScope.launch {
            courseRepository.getCourses()
                .onSuccess { courses ->
                    _uiState.update { it.copy(trendingCourses = courses.take(5)) }
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        _queryFlow.value = query
    }

    private fun executeSearch(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), error = null, isLoading = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            courseRepository.searchCourses(query)
                .onSuccess { data ->
                    val trimmed = query.trim()
                    val updatedRecent = (_uiState.value.recentSearches
                        .filter { it != trimmed }
                        .let { listOf(trimmed) + it })
                        .take(5)
                    _uiState.update {
                        it.copy(results = data, isLoading = false, error = null, recentSearches = updatedRecent)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Đã xảy ra lỗi khi tìm kiếm")
                    }
                }
        }
    }

    fun removeRecentSearch(item: String) {
        _uiState.update { it.copy(recentSearches = it.recentSearches.filter { s -> s != item }) }
    }

    fun clearRecentSearches() {
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    fun searchByCategory(category: String) {
        onQueryChange(category)
    }
}
