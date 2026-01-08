/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.ui.screen.cameras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.models.Camera
import com.app.bitlearning.repositories.CameraRepository
import com.app.bitlearning.repositories.MainLog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class CamerasUiState(
    val cameras: List<Camera> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CamerasViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val log: MainLog,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CamerasUiState())
    val uiState: StateFlow<CamerasUiState> = _uiState.asStateFlow()

    init {
        loadCameras()
    }

    fun loadCameras() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                cameraRepository.getAllCameras()
                    .catch { exception ->
                        log.e("CamerasViewModel", "Error loading cameras: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to load cameras: ${exception.message}",
                        )
                    }
                    .collect { cameras ->
                        _uiState.value = _uiState.value.copy(
                            cameras = cameras,
                            isLoading = false,
                            error = null,
                        )
                    }
            } catch (e: Exception) {
                log.e("CamerasViewModel", "Unexpected error: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unexpected error occurred",
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        log.i("CamerasViewModel", "onCleared")
    }
}
