/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bitlearning.core.common.components.*
import com.app.bitlearning.core.common.theme.*

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit = {},
    onNavigateToCertificates: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToNotificationSetting: () -> Unit = {},
    onNavigateToPaymentSetting: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            ProfileTopBar(onNavigateBack = onNavigateBack, onEdit = onNavigateToEdit)
        },
    ) { padding ->
        if (uiState.isLoading) {
            BLLoadingIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                // Profile Header
                item {
                    ProfileHeader(
                        name = uiState.user?.name ?: "",
                        memberSince = uiState.user?.memberSince ?: 2024,
                        isPremium = uiState.user?.isPremium ?: false,
                        avatarUrl = uiState.user?.avatar,
                    )
                }

                // Learning & Achievements Section
                item {
                    Spacer(Modifier.height(20.dp))
                    ProfileSectionLabel(label = "HỌC TẬP & THÀNH TỰU")
                    Spacer(Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column {
                            BLMenuItemRow(
                                title = "Chứng chỉ của tôi",
                                icon = Icons.Filled.EmojiEvents,
                                onClick = onNavigateToCertificates,
                            )
                            BLDivider(Modifier.padding(horizontal = 16.dp))
                            BLMenuItemRow(
                                title = "Lịch sử học tập",
                                icon = Icons.Filled.History,
                                onClick = onNavigateToHistory,
                            )
                        }
                    }
                }

                // Account Section
                item {
                    Spacer(Modifier.height(20.dp))
                    ProfileSectionLabel(label = "TÀI KHOẢN & CÀI ĐẶT")
                    Spacer(Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column {
                            BLMenuItemRow(
                                title = "Phương thức thanh toán",
                                icon = Icons.Filled.CreditCard,
                                onClick = onNavigateToPaymentSetting,
                            )
                            BLDivider(Modifier.padding(horizontal = 16.dp))
                            BLMenuItemRow(
                                title = "Cài đặt thông báo",
                                icon = Icons.Filled.Notifications,
                                onClick = onNavigateToNotificationSetting,
                            )
                            BLDivider(Modifier.padding(horizontal = 16.dp))
                            BLMenuItemRow(
                                title = "Trợ giúp & Hỗ trợ",
                                icon = Icons.AutoMirrored.Filled.HelpOutline,
                                onClick = onNavigateToSupport,
                            )
                        }
                    }
                }

                // Logout Button
                item {
                    Spacer(Modifier.height(28.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        TextButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Error,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Đăng xuất",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Error,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                        }
                    }
                }

                // Version
                item {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Phiên bản 2.4.1 (Build 890)",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Đăng xuất?") },
            text = { Text("Bạn có chắc chắn muốn đăng xuất không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(onLogout)
                    },
                ) {
                    Text("Đăng xuất", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Huỷ")
                }
            },
            shape = RoundedCornerShape(20.dp),
        )
    }
}

// ─────────────────────────────────────────────
//  Profile Top Bar
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Hồ sơ",
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
            }
        },
        actions = {
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, "Chỉnh sửa", tint = Primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
    )
}

// ─────────────────────────────────────────────
//  Profile Header Card
// ─────────────────────────────────────────────
@Composable
private fun ProfileHeader(
    name: String,
    memberSince: Int,
    isPremium: Boolean,
    avatarUrl: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BLAvatar(
            imageUrl = avatarUrl,
            size = 96.dp,
            showBadge = true,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = "Thành viên từ $memberSince",
            style = MaterialTheme.typography.bodyMedium,
        )
        if (isPremium) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PremiumContainer)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "HỌC VIÊN PREMIUM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Premium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Section Label
// ─────────────────────────────────────────────
@Composable
private fun ProfileSectionLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
            color = OnSurfaceMuted,
            letterSpacing = 0.8.sp,
        ),
        modifier = Modifier.padding(horizontal = 24.dp),
    )
}
