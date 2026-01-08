/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.ui.screen.detail

import androidx.lifecycle.ViewModel
import com.app.bitlearning.repositories.MainLog
import com.app.bitlearning.repositories.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val log: MainLog,
    private val store: Store,
) : ViewModel() {
    override fun onCleared() {
        log.i("DetailViewModel", "onCleared ${store.getValue("test")}.")
    }
}
