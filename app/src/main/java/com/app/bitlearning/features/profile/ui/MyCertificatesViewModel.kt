/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.domain.model.Certificate
import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.repository.CourseRepository
import com.app.bitlearning.data.repository.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CertificateTab { RECEIVED, PENDING }

sealed interface ReceivedCertificateItem {
    val courseTitle: String

    data class Ready(val certificate: Certificate) : ReceivedCertificateItem {
        override val courseTitle: String = certificate.courseTitle
    }

    data class Error(
        val courseId: Int,
        override val courseTitle: String,
        val thumbnailUrl: String?,
        val message: String,
    ) : ReceivedCertificateItem
}

data class MyCertificatesUiState(
    val receivedCertificates: List<ReceivedCertificateItem> = emptyList(),
    val pendingCertificates: List<Certificate> = emptyList(),
    val selectedTab: CertificateTab = CertificateTab.RECEIVED,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
) {
    val displayedReceivedCertificates: List<ReceivedCertificateItem>
        get() {
            val base = receivedCertificates
            return if (searchQuery.isBlank()) {
                base
            } else {
                base.filter { it.courseTitle.contains(searchQuery, ignoreCase = true) }
            }
        }

    val displayedPendingCertificates: List<Certificate>
        get() = if (searchQuery.isBlank()) {
            pendingCertificates
        } else {
            pendingCertificates.filter { it.courseTitle.contains(searchQuery, ignoreCase = true) }
        }
}

@HiltViewModel
class MyCertificatesViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyCertificatesUiState())
    val uiState: StateFlow<MyCertificatesUiState> = _uiState

    init {
        loadCertificates()
    }

    fun loadCertificates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            courseRepository.getMyCourses(page = 0, size = 100)
                .onSuccess { courses ->
                    val completed = courses.filter { it.isCompleted || it.progress >= 1f }
                    val pending = courses
                        .filterNot { it.isCompleted || it.progress >= 1f }
                        .map(::courseToPendingCertificate)
                    val received = completed.map { course -> toCertificateItem(course) }
                    _uiState.update {
                        it.copy(
                            receivedCertificates = received,
                            pendingCertificates = pending,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toUserMessage("Không thể tải danh sách chứng chỉ"),
                        )
                    }
                }
        }
    }

    fun selectTab(tab: CertificateTab) = _uiState.update { it.copy(selectedTab = tab) }

    fun onSearchQueryChange(query: String) = _uiState.update { it.copy(searchQuery = query) }

    fun retryCertificate(courseId: Int, courseTitle: String, thumbnailUrl: String?) {
        viewModelScope.launch {
            val item = courseRepository.getCertificate(courseId, courseTitle).fold(
                onSuccess = { ReceivedCertificateItem.Ready(it) },
                onFailure = {
                    ReceivedCertificateItem.Error(
                        courseId = courseId,
                        courseTitle = courseTitle,
                        thumbnailUrl = thumbnailUrl,
                        message = it.toUserMessage("Không thể tải chứng chỉ"),
                    )
                },
            )
            _uiState.update { state ->
                state.copy(
                    receivedCertificates = state.receivedCertificates.map { current ->
                        if (current is ReceivedCertificateItem.Error && current.courseId == courseId) {
                            item
                        } else {
                            current
                        }
                    },
                )
            }
        }
    }

    private suspend fun toCertificateItem(course: Course): ReceivedCertificateItem =
        courseRepository.getCertificate(course.id, course.title).fold(
            onSuccess = { ReceivedCertificateItem.Ready(it) },
            onFailure = {
                ReceivedCertificateItem.Error(
                    courseId = course.id,
                    courseTitle = course.title,
                    thumbnailUrl = course.thumbnailUrl,
                    message = it.toUserMessage("Không thể tải chứng chỉ"),
                )
            },
        )
}

private fun courseToPendingCertificate(course: Course): Certificate = Certificate(
    id = "pending-${course.id}",
    courseId = course.id,
    courseTitle = course.title,
    issuedDate = "Đang học ${(course.progress * 100).toInt()}%",
    thumbnailUrl = course.thumbnailUrl,
)
