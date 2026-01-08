/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.ui.screen.home

import androidx.lifecycle.ViewModel
import com.app.bitlearning.repositories.MainLog
import com.app.bitlearning.repositories.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val test: String,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val log: MainLog?,
    private val store: Store?,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(""))
    val uiState = _uiState.asStateFlow()

    fun updateTest(s: String) {
        _uiState.value = _uiState.value.copy(test = s)
    }

    override fun onCleared() {
        log?.i("HomeViewModel", "onCleared ${store?.getValue("test")}.")
    }
}
