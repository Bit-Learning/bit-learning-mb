/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.player.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.model.Lecture
import com.app.bitlearning.domain.repository.CourseRepository
import com.app.bitlearning.domain.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayerUiState(
    val course: Course? = null,
    val lessons: List<Lecture> = emptyList(),
    val currentLesson: Lecture? = null,
    val currentVideoUrl: String? = null,
    val isLoading: Boolean = true,
    val isAutoPlay: Boolean = true,
    // 0=Bài học, 1=Giới thiệu, 2=Tài liệu
    val selectedTab: Int = 0,
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val lessonRepository: LessonRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val courseId: Int = savedStateHandle.get<String>("courseId")?.toIntOrNull() ?: 0
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val course = courseRepository.getCourseById(courseId).getOrNull()
            val lessons = lessonRepository.getLessonsForCourse(courseId).getOrElse { emptyList() }
            val current = lessons.firstOrNull()
            _uiState.update {
                it.copy(
                    course = course,
                    lessons = lessons,
                    currentLesson = current,
                    currentVideoUrl = null,
                    isLoading = false,
                )
            }
        }
    }

    fun selectLesson(lesson: Lecture) {
        if (lesson.isLocked) return
        _uiState.update {
            it.copy(
                currentLesson = lesson,
                currentVideoUrl = null, // video URL comes from lecture content API
            )
        }
    }

    fun markCurrentCompleted() {
        val currentId = _uiState.value.currentLesson?.id ?: return
        viewModelScope.launch {
            lessonRepository.markLectureCompleted(currentId)
            _uiState.update { state ->
                state.copy(
                    lessons = state.lessons.map { lesson ->
                        if (lesson.id == currentId) lesson.copy(isCompleted = true) else lesson
                    },
                )
            }
        }
    }

    fun toggleAutoPlay() = _uiState.update { it.copy(isAutoPlay = !it.isAutoPlay) }
    fun selectTab(tab: Int) = _uiState.update { it.copy(selectedTab = tab) }
}
