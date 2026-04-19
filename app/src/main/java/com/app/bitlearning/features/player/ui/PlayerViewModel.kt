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
import com.app.bitlearning.core.preferences.AppPreferences
import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.model.Lecture
import com.app.bitlearning.domain.model.LectureQuizContent
import com.app.bitlearning.domain.model.LectureTextContent
import com.app.bitlearning.domain.model.LectureType
import com.app.bitlearning.domain.model.Section
import com.app.bitlearning.domain.model.SyncProgressRequest
import com.app.bitlearning.domain.repository.CourseRepository
import com.app.bitlearning.domain.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayerUiState(
    val course: Course? = null,
    val sections: List<Section> = emptyList(),
    val lessons: List<Lecture> = emptyList(),
    val currentLesson: Lecture? = null,
    val currentVideoUrl: String? = null,
    val currentTextContent: LectureTextContent? = null,
    val currentQuizContent: LectureQuizContent? = null,
    val lastWatchedSecond: Int = 0,
    val hasAccess: Boolean = false,
    val authToken: String? = null,
    val isLoading: Boolean = true,
    val isAutoPlay: Boolean = true,
    val selectedTab: Int = 0,
    val error: String? = null,
) {
    val canAccessCurrentLesson: Boolean
        get() = currentLesson?.let { hasAccess || it.isPreviewable } ?: false
}

private const val LESSON_LIST_TAB = 0
private const val COURSE_INFO_TAB = 1
private const val LESSON_CONTENT_TAB = 2

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val lessonRepository: LessonRepository,
    private val appPreferences: AppPreferences,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val courseId: Int = savedStateHandle.get<String>("courseId")?.toIntOrNull() ?: 0
    private val selectedLectureIdArg: Int = savedStateHandle.get<String>("lectureId")?.toIntOrNull() ?: 0

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState
    private var lessonContentJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            runCatching {
                val tokenDeferred = async { appPreferences.accessToken.first() }
                val courseDeferred = async { courseRepository.getCourseById(courseId).getOrThrow() }
                val sectionsDeferred = async { lessonRepository.getSectionsByCourse(courseId).getOrThrow() }
                val accessDeferred = async { courseRepository.checkCourseAccess(courseId).getOrElse { false } }
                val progressDeferred = async { courseRepository.getCourseProgress(courseId).getOrElse { 0f } }
                val lastOpenedDeferred = async { appPreferences.getLastOpenedLectureId(courseId) }

                val sections = sectionsDeferred.await().filterNot { it.isDeleted }
                val lessons = sections.flatMap { section -> section.lectures.filterNot { it.isDeleted } }
                    .sortedBy { it.orderIndex }
                val hasAccess = accessDeferred.await()
                val course = courseDeferred.await().copy(
                    sections = sections,
                    hasAccess = hasAccess,
                    progress = progressDeferred.await(),
                )
                val initialLesson = resolveInitialLesson(
                    lessons = lessons,
                    hasAccess = hasAccess,
                    lastOpenedLectureId = lastOpenedDeferred.await(),
                )
                    ?: error("Không tìm thấy bài học hợp lệ")

                _uiState.update {
                    it.copy(
                        course = course,
                        sections = sections,
                        lessons = lessons.map { lesson -> lesson.copy(isLocked = !hasAccess && !lesson.isPreviewable) },
                        currentLesson = initialLesson.copy(isLocked = !hasAccess && !initialLesson.isPreviewable),
                        hasAccess = hasAccess,
                        authToken = tokenDeferred.await(),
                        selectedTab = LESSON_LIST_TAB,
                        isLoading = false,
                    )
                }

                loadCurrentLessonContent(initialLesson.id, switchToContent = false)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Không thể tải bài học",
                    )
                }
            }
        }
    }

    private fun resolveInitialLesson(
        lessons: List<Lecture>,
        hasAccess: Boolean,
        lastOpenedLectureId: Int?,
    ): Lecture? {
        val requested = lessons.firstOrNull { it.id == selectedLectureIdArg && (hasAccess || it.isPreviewable) }
        if (requested != null) return requested

        val lastOpened = lessons.firstOrNull {
            it.id == lastOpenedLectureId && (hasAccess || it.isPreviewable)
        }
        if (lastOpened != null) return lastOpened

        val firstIncomplete = lessons.firstOrNull {
            (hasAccess || it.isPreviewable) && !it.isCompleted
        }
        if (firstIncomplete != null) return firstIncomplete

        return lessons.firstOrNull { hasAccess || it.isPreviewable }
    }

    private fun loadCurrentLessonContent(lectureId: Int, switchToContent: Boolean = false) {
        val lecture = _uiState.value.lessons.firstOrNull { it.id == lectureId } ?: return
        if (lecture.isLocked) return

        lessonContentJob?.cancel()
        lessonContentJob = viewModelScope.launch {
            appPreferences.saveLastOpenedLectureId(courseId, lectureId)
            _uiState.update {
                it.copy(
                    currentLesson = lecture,
                    currentVideoUrl = null,
                    currentTextContent = null,
                    currentQuizContent = null,
                    lastWatchedSecond = 0,
                    selectedTab = if (switchToContent && lecture.type != LectureType.VIDEO) LESSON_CONTENT_TAB else it.selectedTab,
                    error = null,
                )
            }

            when (lecture.type) {
                LectureType.VIDEO -> {
                    val videoUrl = lessonRepository.getVideoM3u8Url(lectureId).getOrNull()
                    val lastWatched = lessonRepository.getLectureProgress(lectureId).getOrElse { 0 }
                    val isCompleted = lessonRepository.isLectureCompleted(lectureId).getOrElse { lecture.isCompleted }
                    _uiState.update {
                        it.copy(
                            currentVideoUrl = videoUrl,
                            lastWatchedSecond = lastWatched,
                            lessons = updateLecture(it.lessons, lectureId) { item ->
                                item.copy(
                                    isCompleted = isCompleted,
                                    progress = when {
                                        isCompleted -> 1f
                                        lastWatched > 0 -> maxOf(item.progress, 0.01f)
                                        else -> item.progress
                                    },
                                )
                            },
                            currentLesson = it.currentLesson?.copy(
                                isCompleted = isCompleted,
                                progress = when {
                                    isCompleted -> 1f
                                    lastWatched > 0 -> maxOf(it.currentLesson.progress, 0.01f)
                                    else -> it.currentLesson.progress
                                },
                            ),
                        )
                    }
                }

                LectureType.TEXT -> {
                    val textResult = lessonRepository.getLectureText(lectureId)
                    if (textResult.isSuccess) {
                        val text = textResult.getOrNull()
                        val isCompleted = lessonRepository.isLectureCompleted(lectureId).getOrElse { lecture.isCompleted }
                        _uiState.update {
                            it.copy(
                                currentTextContent = text,
                                lessons = updateLecture(it.lessons, lectureId) { item ->
                                    item.copy(isCompleted = isCompleted, progress = if (isCompleted) 1f else item.progress)
                                },
                                currentLesson = it.currentLesson?.copy(
                                    isCompleted = isCompleted,
                                    progress = if (isCompleted) 1f else it.currentLesson.progress,
                                ),
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(error = textResult.exceptionOrNull()?.message ?: "Không tải được nội dung bài học")
                        }
                    }
                }

                LectureType.QUIZ -> {
                    val quizResult = lessonRepository.getLectureQuiz(lectureId)
                    if (quizResult.isSuccess) {
                        val quiz = quizResult.getOrNull()
                        val isCompleted = lessonRepository.isLectureCompleted(lectureId).getOrElse { lecture.isCompleted }
                        _uiState.update {
                            it.copy(
                                currentQuizContent = quiz,
                                lessons = updateLecture(it.lessons, lectureId) { item ->
                                    item.copy(isCompleted = isCompleted, progress = if (isCompleted) 1f else item.progress)
                                },
                                currentLesson = it.currentLesson?.copy(
                                    isCompleted = isCompleted,
                                    progress = if (isCompleted) 1f else it.currentLesson.progress,
                                ),
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(error = quizResult.exceptionOrNull()?.message ?: "Không tải được bài tập")
                        }
                    }
                }
            }
        }
    }

    fun selectLesson(lesson: Lecture) {
        if (lesson.isLocked) return
        loadCurrentLessonContent(
            lectureId = lesson.id,
            switchToContent = lesson.type == LectureType.TEXT || lesson.type == LectureType.QUIZ,
        )
    }

    fun syncCurrentProgress(currentSecond: Int, totalDuration: Int) {
        val currentId = _uiState.value.currentLesson?.id ?: return
        if (currentSecond <= 0 || totalDuration <= 0) return

        viewModelScope.launch {
            val result = lessonRepository.syncProgress(
                SyncProgressRequest(
                    lectureId = currentId,
                    currentSecond = currentSecond,
                    totalDuration = totalDuration,
                ),
            )
            if (result.isSuccess) {
                _uiState.update { state ->
                    val syncedProgress =
                        if (currentSecond > 0) maxOf(state.currentLesson?.progress ?: 0f, 0.01f) else state.currentLesson?.progress ?: 0f
                    state.copy(
                        lessons = updateLecture(state.lessons, currentId) { it.copy(progress = syncedProgress) },
                        currentLesson = state.currentLesson?.copy(progress = syncedProgress),
                        lastWatchedSecond = currentSecond,
                    )
                }
            }
        }
    }

    fun markCurrentCompleted() {
        val currentId = _uiState.value.currentLesson?.id ?: return
        viewModelScope.launch {
            lessonRepository.markLectureCompleted(currentId)
            refreshCourseProgress()
            _uiState.update { state ->
                state.copy(
                    lessons = updateLecture(state.lessons, currentId) { it.copy(isCompleted = true, progress = 1f) },
                    currentLesson = state.currentLesson?.copy(isCompleted = true, progress = 1f),
                )
            }
        }
    }

    private suspend fun refreshCourseProgress() {
        val progress = courseRepository.getCourseProgress(courseId).getOrElse { _uiState.value.course?.progress ?: 0f }
        _uiState.update { state ->
            state.copy(course = state.course?.copy(progress = progress))
        }
    }

    fun toggleAutoPlay() = _uiState.update { it.copy(isAutoPlay = !it.isAutoPlay) }
    fun selectTab(tab: Int) = _uiState.update { it.copy(selectedTab = tab) }
}

private fun updateLecture(
    lessons: List<Lecture>,
    lectureId: Int,
    transform: (Lecture) -> Lecture,
): List<Lecture> = lessons.map { lecture ->
    if (lecture.id == lectureId) transform(lecture) else lecture
}
