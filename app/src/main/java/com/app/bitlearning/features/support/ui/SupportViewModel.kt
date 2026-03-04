/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.support.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class FaqItem(
    val id: String,
    val icon: String, // semantic icon name key
    val question: String,
)

data class SupportUiState(
    val searchQuery: String = "",
    val faqItems: List<FaqItem> = defaultFaqs(),
) {
    val filteredFaqs: List<FaqItem>
        get() = if (searchQuery.isBlank()) {
            faqItems
        } else {
            faqItems.filter { it.question.contains(searchQuery, ignoreCase = true) }
        }
}

@HiltViewModel
class SupportViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState: StateFlow<SupportUiState> = _uiState

    fun onQueryChange(query: String) = _uiState.update { it.copy(searchQuery = query) }
}

private fun defaultFaqs() = listOf(
    FaqItem("f1", "payments", "Làm thế nào để gia hạn khóa học?"),
    FaqItem("f2", "school", "Tôi không thể xem video bài giảng"),
    FaqItem("f3", "account_circle", "Quên mật khẩu đăng nhập"),
    FaqItem("f4", "verified_user", "Chính sách bảo mật thông tin"),
)
