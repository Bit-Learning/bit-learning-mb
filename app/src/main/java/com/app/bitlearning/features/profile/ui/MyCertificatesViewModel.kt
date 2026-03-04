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
import com.app.bitlearning.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CertificateTab { RECEIVED, PENDING }

data class MyCertificatesUiState(
    val receivedCertificates: List<Certificate> = emptyList(),
    val pendingCertificates: List<Certificate> = emptyList(),
    val selectedTab: CertificateTab = CertificateTab.RECEIVED,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
) {
    val displayedCertificates: List<Certificate>
        get() {
            val base = if (selectedTab == CertificateTab.RECEIVED) receivedCertificates else pendingCertificates
            return if (searchQuery.isBlank()) {
                base
            } else {
                base.filter { it.courseTitle.contains(searchQuery, ignoreCase = true) }
            }
        }
}

@HiltViewModel
class MyCertificatesViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyCertificatesUiState())
    val uiState: StateFlow<MyCertificatesUiState> = _uiState

    init {
        loadCertificates()
    }

    private fun loadCertificates() {
        viewModelScope.launch {
            val certs = userRepository.getCertificates().getOrElse { emptyList() }
            _uiState.update {
                it.copy(
                    receivedCertificates = certs,
                    pendingCertificates = emptyList(),
                    isLoading = false,
                )
            }
        }
    }

    fun selectTab(tab: CertificateTab) = _uiState.update { it.copy(selectedTab = tab) }

    fun onSearchQueryChange(query: String) = _uiState.update { it.copy(searchQuery = query) }
}
