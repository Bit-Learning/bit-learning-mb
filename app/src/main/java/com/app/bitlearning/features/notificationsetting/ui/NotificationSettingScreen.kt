/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.notificationsetting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bitlearning.core.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cài đặt thông báo",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
        ) {
            // ── Push notifications ────────────────────────────────────────
            item {
                NotifSection(
                    icon = Icons.Filled.NotificationsActive,
                    title = "Thông báo đẩy",
                ) {
                    NotifToggleRow(
                        icon = Icons.Filled.Schedule,
                        iconBg = PrimaryContainer,
                        iconTint = Primary,
                        title = "Nhắc nhở học tập",
                        subtitle = "Thông báo khi đến giờ học bài",
                        checked = state.studyReminder,
                        onCheckedChange = viewModel::setStudyReminder,
                        isLast = false,
                    )
                    NotifToggleRow(
                        icon = Icons.Filled.School,
                        iconBg = PrimaryContainer,
                        iconTint = Primary,
                        title = "Khóa học mới",
                        subtitle = "Cập nhật khi có nội dung mới",
                        checked = state.newCourseContent,
                        onCheckedChange = viewModel::setNewCourseContent,
                        isLast = false,
                    )
                    NotifToggleRow(
                        icon = Icons.Filled.Sell,
                        iconBg = PrimaryContainer,
                        iconTint = Primary,
                        title = "Khuyến mãi",
                        subtitle = "Ưu đãi và voucher học tập",
                        checked = state.promotions,
                        onCheckedChange = viewModel::setPromotions,
                        isLast = false,
                    )
                    NotifToggleRow(
                        icon = Icons.Filled.SettingsSuggest,
                        iconBg = PrimaryContainer,
                        iconTint = Primary,
                        title = "Cập nhật hệ thống",
                        subtitle = "Thông tin bảo trì & tính năng",
                        checked = state.systemUpdates,
                        onCheckedChange = viewModel::setSystemUpdates,
                        isLast = true,
                    )
                }
            }

            // ── Email notifications ───────────────────────────────────────
            item {
                NotifSection(
                    icon = Icons.Filled.Mail,
                    title = "Thông báo qua Email",
                ) {
                    NotifToggleRow(
                        icon = Icons.Filled.HistoryEdu,
                        iconBg = PrimaryContainer.copy(alpha = 0.5f),
                        iconTint = Primary.copy(alpha = 0.7f),
                        title = "Báo cáo học tập tuần",
                        subtitle = "Tổng kết tiến độ qua email",
                        checked = state.weeklyReport,
                        onCheckedChange = viewModel::setWeeklyReport,
                        isLast = false,
                    )
                    NotifToggleRow(
                        icon = Icons.Filled.Campaign,
                        iconBg = PrimaryContainer.copy(alpha = 0.5f),
                        iconTint = Primary.copy(alpha = 0.7f),
                        title = "Tin tức & Sự kiện",
                        subtitle = "Hội thảo và bài viết mới nhất",
                        checked = state.newsAndEvents,
                        onCheckedChange = viewModel::setNewsAndEvents,
                        isLast = false,
                    )
                    // Security toggle — always on, disabled
                    NotifToggleRow(
                        icon = Icons.Filled.VerifiedUser,
                        iconBg = PrimaryContainer.copy(alpha = 0.5f),
                        iconTint = Primary.copy(alpha = 0.7f),
                        title = "Bảo mật tài khoản",
                        subtitle = "Cảnh báo đăng nhập & đổi mật khẩu",
                        checked = true,
                        onCheckedChange = {},
                        enabled = false,
                        isLast = true,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Thông báo bảo mật không thể tắt".uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnSurfaceMuted,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }

            // ── Tip banner ────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryContainer.copy(alpha = 0.6f))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Mẹo nhỏ",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = "Việc bật thông báo nhắc nhở giúp bạn duy trì thói quen học tập tốt hơn 40% so với bình thường.",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Section container with header
// ─────────────────────────────────────────────
@Composable
private fun NotifSection(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall)
        }
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column { content() }
        }
    }
}

// ─────────────────────────────────────────────
//  Single toggle row
// ─────────────────────────────────────────────
@Composable
private fun NotifToggleRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    isLast: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                )
            }
            // Text
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                )
            }
            // Switch
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = OnSurfaceMuted.copy(alpha = 0.4f),
                    disabledCheckedTrackColor = Primary.copy(alpha = 0.5f),
                    disabledCheckedThumbColor = Color.White,
                ),
            )
        }
        if (!isLast) HorizontalDivider(color = Divider, thickness = 0.5.dp)
    }
}
