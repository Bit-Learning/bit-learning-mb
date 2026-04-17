/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.notification.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// ─────────────────────────────────────────────
//  Domain models
// ─────────────────────────────────────────────
enum class NotificationCategory(val label: String) {
    ALL("Tất cả"),
    COURSE("Khóa học"),
    PROMO("Khuyến mãi"),
}

enum class NotificationType { LESSON, REMINDER, PROMO, ACHIEVEMENT, COMMUNITY }

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timeLabel: String,
    val isUnread: Boolean = true,
    val groupLabel: String? = null, // e.g. "Hôm qua", null = "Hôm nay"
)

// ─────────────────────────────────────────────
//  UI State
// ─────────────────────────────────────────────
data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList(),
    val selectedCategory: NotificationCategory = NotificationCategory.ALL,
) {
    val filtered: List<AppNotification>
        get() = when (selectedCategory) {
            NotificationCategory.ALL -> notifications
            NotificationCategory.COURSE -> notifications.filter {
                it.type == NotificationType.LESSON ||
                    it.type == NotificationType.REMINDER ||
                    it.type == NotificationType.ACHIEVEMENT ||
                    it.type == NotificationType.COMMUNITY
            }
            NotificationCategory.PROMO -> notifications.filter { it.type == NotificationType.PROMO }
        }

    val unreadCount: Int get() = notifications.count { it.isUnread }
}

// ─────────────────────────────────────────────
//  ViewModel
// ─────────────────────────────────────────────
@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState(notifications = mockNotifications()))
    val uiState: StateFlow<NotificationUiState> = _uiState

    fun selectCategory(category: NotificationCategory) = _uiState.update { it.copy(selectedCategory = category) }

    fun markAllRead() = _uiState.update { s -> s.copy(notifications = s.notifications.map { it.copy(isUnread = false) }) }

    fun markRead(id: String) = _uiState.update { s -> s.copy(notifications = s.notifications.map { if (it.id == id) it.copy(isUnread = false) else it }) }
}

// ─────────────────────────────────────────────
//  Mock data
// ─────────────────────────────────────────────
private fun mockNotifications() = listOf(
    AppNotification(
        id = "n1",
        type = NotificationType.LESSON,
        title = "Bài học mới đã sẵn sàng",
        body = "Khóa học \"Lập trình Python\" vừa cập nhật chương 5. Cùng khám phá ngay!",
        timeLabel = "2 giờ trước",
        isUnread = true,
    ),
    AppNotification(
        id = "n2",
        type = NotificationType.REMINDER,
        title = "Nhắc nhở học tập",
        body = "Đã đến lúc ôn tập kiến thức về biến và vòng lặp rồi bạn ơi. Dành ra 15 phút nhé!",
        timeLabel = "5 giờ trước",
        isUnread = true,
    ),
    AppNotification(
        id = "n3",
        type = NotificationType.PROMO,
        title = "Ưu đãi 50% cho khóa học mới",
        body = "Đừng bỏ lỡ cơ hội sở hữu khóa học \"Lập trình Web Full-stack\" với giá cực hời.",
        timeLabel = "12 giờ trước",
        isUnread = false,
    ),
    AppNotification(
        id = "n4",
        type = NotificationType.ACHIEVEMENT,
        title = "Chúc mừng bạn!",
        body = "Bạn đã hoàn thành 100% khóa học \"Lập trình cơ bản\". Tải chứng chỉ ngay.",
        timeLabel = "24 giờ trước",
        isUnread = false,
        groupLabel = "Hôm qua",
    ),
    AppNotification(
        id = "n5",
        type = NotificationType.COMMUNITY,
        title = "Phản hồi mới từ giảng viên",
        body = "Giảng viên Sơn Tùng vừa trả lời thắc mắc của bạn trong bài giảng số 12.",
        timeLabel = "Hôm qua",
        isUnread = false,
        groupLabel = "Hôm qua",
    ),
)
