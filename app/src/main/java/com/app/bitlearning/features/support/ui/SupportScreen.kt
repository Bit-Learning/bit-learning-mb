/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.support.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bitlearning.core.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: SupportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trợ giúp & Hỗ trợ",
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
        bottomBar = {
            SupportBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToCourses = onNavigateToCourses,
                onNavigateToProfile = onNavigateToProfile,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // ── Search bar ────────────────────────────────────────────────
            item {
                SupportSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                )
            }

            // ── Quick contact cards ───────────────────────────────────────
            item {
                Text(
                    text = "Liên hệ nhanh",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    QuickContactCard(
                        icon = Icons.Filled.ChatBubble,
                        title = "Chat trực tuyến",
                        subtitle = "Hỗ trợ ngay lập tức",
                        buttonText = "Bắt đầu chat",
                        buttonPrimary = true,
                        modifier = Modifier.weight(1f),
                        onClick = {},
                    )
                    QuickContactCard(
                        icon = Icons.Filled.Mail,
                        title = "Gửi yêu cầu",
                        subtitle = "Phản hồi trong 24h",
                        buttonText = "Tạo vé hỗ trợ",
                        buttonPrimary = false,
                        modifier = Modifier.weight(1f),
                        onClick = {},
                    )
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── FAQ section header ────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Câu hỏi thường gặp",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "Xem tất cả",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Primary,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier.clickable { },
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── FAQ items ─────────────────────────────────────────────────
            if (uiState.filteredFaqs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Không tìm thấy câu hỏi phù hợp",
                            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                        )
                    }
                }
            } else {
                items(uiState.filteredFaqs, key = { it.id }) { faq ->
                    FaqRow(faq = faq)
                }
            }

            // ── Hotline banner ────────────────────────────────────────────
            item {
                Spacer(Modifier.height(24.dp))
                HotlineBanner(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Search Bar
// ─────────────────────────────────────────────
@Composable
private fun SupportSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface)
            .padding(horizontal = 14.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(22.dp),
        )
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Tìm kiếm câu hỏi thường gặp...",
                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Primary,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        )
        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Xóa",
                    tint = OnSurfaceMuted,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Quick Contact Card
// ─────────────────────────────────────────────
@Composable
private fun QuickContactCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    buttonText: String,
    buttonPrimary: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(26.dp),
                )
            }

            // Texts
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                )
            }

            // Button
            if (buttonPrimary) {
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontSize = 13.sp,
                        ),
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                        ),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  FAQ Row
// ─────────────────────────────────────────────
@Composable
private fun FaqRow(faq: FaqItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Icon(
                imageVector = faqIcon(faq.icon),
                contentDescription = null,
                tint = Primary.copy(alpha = 0.65f),
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = faq.question,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = OnSurfaceMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Hotline Banner
// ─────────────────────────────────────────────
@Composable
private fun HotlineBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Primary, PrimaryLight),
                ),
            )
            .padding(24.dp),
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp),
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 20.dp),
        )

        // Content
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Cần hỗ trợ gấp?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = "Gọi cho chúng tôi ngay để được giải đáp mọi thắc mắc 24/7.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.85f),
                ),
            )
            Spacer(Modifier.height(6.dp))
            Button(
                onClick = {},
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(2.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "1900 1234",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                    ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Bottom Navigation Bar
// ─────────────────────────────────────────────
@Composable
private fun SupportBottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Primary,
        selectedTextColor = Primary,
        unselectedIconColor = OnSurfaceMuted,
        unselectedTextColor = OnSurfaceMuted,
        indicatorColor = PrimaryContainer,
    )
    NavigationBar(
        containerColor = CardSurface,
        tonalElevation = 0.dp,
    ) {
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
            icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null) },
            label = { Text("Hỗ trợ", style = MaterialTheme.typography.labelSmall) },
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

// ─────────────────────────────────────────────
//  Icon mapper
// ─────────────────────────────────────────────
private fun faqIcon(key: String): ImageVector = when (key) {
    "payments" -> Icons.Filled.Payments
    "school" -> Icons.Filled.School
    "account_circle" -> Icons.Filled.AccountCircle
    "verified_user" -> Icons.Filled.VerifiedUser
    else -> Icons.AutoMirrored.Filled.HelpOutline
}
