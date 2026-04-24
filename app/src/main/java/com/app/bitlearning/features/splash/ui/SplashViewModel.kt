/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.splash.ui

import androidx.lifecycle.ViewModel
import com.app.bitlearning.core.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {

    /** Returns true if a non-blank access token is stored (user was previously logged in). */
    suspend fun hasStoredToken(): Boolean =
        prefs.accessToken.first()?.isNotBlank() == true
}
