/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.notificationsetting.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class NotificationSettingsState(
    // Push
    val studyReminder: Boolean = true,
    val newCourseContent: Boolean = true,
    val promotions: Boolean = false,
    val systemUpdates: Boolean = true,
    // Email
    val weeklyReport: Boolean = true,
    val newsAndEvents: Boolean = false,
    // security is always on — read-only
)

@HiltViewModel
class NotificationSettingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(NotificationSettingsState())
    val state: StateFlow<NotificationSettingsState> = _state

    fun setStudyReminder(on: Boolean) = _state.update { it.copy(studyReminder = on) }
    fun setNewCourseContent(on: Boolean) = _state.update { it.copy(newCourseContent = on) }
    fun setPromotions(on: Boolean) = _state.update { it.copy(promotions = on) }
    fun setSystemUpdates(on: Boolean) = _state.update { it.copy(systemUpdates = on) }
    fun setWeeklyReport(on: Boolean) = _state.update { it.copy(weeklyReport = on) }
    fun setNewsAndEvents(on: Boolean) = _state.update { it.copy(newsAndEvents = on) }
}
