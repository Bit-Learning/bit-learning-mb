/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.notification.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thông báo",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.markAllRead() }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Thêm",
                            tint = OnSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardSurface),
            )
        },
        bottomBar = {
            NotificationBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToCourses = onNavigateToCourses,
                onNavigateToProfile = onNavigateToProfile,
                hasUnread = uiState.unreadCount > 0,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // ── Category chips ────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    NotificationCategory.entries.forEach { cat ->
                        CategoryChip(
                            label = cat.label,
                            selected = uiState.selectedCategory == cat,
                            onClick = { viewModel.selectCategory(cat) },
                        )
                    }
                }
            }

            // ── Notification items with group headers ─────────────────────
            val items = uiState.filtered
            var lastGroup: String? = "shown" // sentinel so first group (null = today) renders correctly

            itemsIndexed(items) { index, notif ->
                // Emit section label when the groupLabel changes
                val currentGroup = notif.groupLabel
                val showHeader = currentGroup != null && currentGroup != lastGroup
                lastGroup = currentGroup

                if (showHeader) {
                    Text(
                        text = currentGroup,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceMuted,
                            letterSpacing = 1.2.sp,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = if (index == 0) 0.dp else 8.dp, bottom = 4.dp),
                    )
                }

                NotificationItem(
                    notification = notif,
                    onClick = { viewModel.markRead(notif.id) },
                )
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

// ─────────────────────────────────────────────
//  Category Chip
// ─────────────────────────────────────────────
@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(CircleShape)
            .background(if (selected) Primary else SurfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else OnSurfaceVariant,
                fontSize = 13.sp,
            ),
        )
    }
}

// ─────────────────────────────────────────────
//  Single notification row
// ─────────────────────────────────────────────
@Composable
private fun NotificationItem(
    notification: AppNotification,
    onClick: () -> Unit,
) {
    val (iconVector, iconBg, iconTint) = notificationStyle(notification.type)
    val cardBg = if (notification.isUnread) Primary.copy(alpha = 0.05f) else CardSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .then(
                Modifier.clickable { onClick() },
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(26.dp),
            )
        }

        // Text content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = notification.timeLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnSurfaceMuted,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp,
                    ),
                )
            }
            Text(
                text = notification.body,
                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant),
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Style helper per notification type
// ─────────────────────────────────────────────
private data class NotifStyle(val icon: ImageVector, val bg: Color, val tint: Color)

@Composable
private fun notificationStyle(type: NotificationType): NotifStyle = when (type) {
    NotificationType.LESSON -> NotifStyle(
        icon = Icons.AutoMirrored.Filled.MenuBook,
        bg = PrimaryContainer,
        tint = Primary,
    )
    NotificationType.REMINDER -> NotifStyle(
        icon = Icons.Filled.NotificationsActive,
        bg = Color(0xFFFFF3E0),
        tint = Color(0xFFE65100),
    )
    NotificationType.PROMO -> NotifStyle(
        icon = Icons.Filled.Sell,
        bg = Color(0xFFE8F5E9),
        tint = Color(0xFF2E7D32),
    )
    NotificationType.ACHIEVEMENT -> NotifStyle(
        icon = Icons.Filled.WorkspacePremium,
        bg = Color(0xFFF3E5F5),
        tint = Color(0xFF7B1FA2),
    )
    NotificationType.COMMUNITY -> NotifStyle(
        icon = Icons.Filled.ChatBubble,
        bg = Color(0xFFE3F2FD),
        tint = Color(0xFF1565C0),
    )
}

// ─────────────────────────────────────────────
//  Bottom Navigation Bar (custom for this screen)
// ─────────────────────────────────────────────
@Composable
private fun NotificationBottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
    hasUnread: Boolean,
) {
    NavigationBar(
        containerColor = CardSurface,
        tonalElevation = 0.dp,
    ) {
        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Primary,
            selectedTextColor = Primary,
            unselectedIconColor = OnSurfaceMuted,
            unselectedTextColor = OnSurfaceMuted,
            indicatorColor = PrimaryContainer,
        )

        NavigationBarItem(
            selected = false,
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Trang chủ", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToCourses,
            icon = { Icon(Icons.Filled.School, contentDescription = null) },
            label = { Text("Khóa học", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                BadgedBox(
                    badge = {
                        if (hasUnread) Badge(containerColor = Primary)
                    },
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null)
                }
            },
            label = { Text("Thông báo", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Cá nhân", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
    }
}
