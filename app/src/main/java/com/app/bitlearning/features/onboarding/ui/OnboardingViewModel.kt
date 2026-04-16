/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 */
package com.app.bitlearning.features.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.core.preferences.AppPreferences
import com.app.bitlearning.domain.model.OnboardingPage
import com.app.bitlearning.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val pages: List<OnboardingPage> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val prefs: AppPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    init {
        loadPages()
    }

    private fun loadPages() {
        viewModelScope.launch {
            onboardingRepository.getOnboardingPages()
                .onSuccess { pages ->
                    val finalPages = pages.ifEmpty { defaultPages() }
                    _uiState.update { it.copy(pages = finalPages, isLoading = false) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(pages = defaultPages(), isLoading = false)
                    }
                }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            prefs.setOnboardingCompleted(true)
        }
    }

    private fun defaultPages() = listOf(
        OnboardingPage(
            id = 1,
            imageUrl = "https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=600",
            title = "Làm chủ kỹ năng\nmới mọi lúc, mọi nơi",
            description = "Tiếp cận hàng ngàn khóa học từ chuyên gia về công nghệ, thiết kế, kinh doanh và hơn thế nữa.",
            orderIndex = 0,
        ),
        OnboardingPage(
            id = 2,
            imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600",
            title = "Học từ chuyên gia\nhàng đầu thế giới",
            description = "Các khóa học được thiết kế bởi những chuyên gia thực tế, cập nhật liên tục.",
            orderIndex = 1,
        ),
        OnboardingPage(
            id = 3,
            imageUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=600",
            title = "Nhận chứng chỉ\ncó giá trị thực tế",
            description = "Hoàn thành khóa học và nhận chứng chỉ được công nhận rộng rãi.",
            orderIndex = 2,
        ),
    )
}
