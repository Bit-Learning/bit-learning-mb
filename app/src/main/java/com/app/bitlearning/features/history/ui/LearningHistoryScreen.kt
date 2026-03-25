/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.history.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.BLLoadingIndicator
import com.app.bitlearning.core.common.theme.*
import com.app.bitlearning.domain.model.Course

// ─────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningHistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCourse: (Int) -> Unit,
    onNavigateToCertificates: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: LearningHistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        HistorySearchField(
                            query = uiState.searchQuery,
                            onQueryChange = viewModel::onQueryChange,
                            onClose = {
                                showSearch = false
                                viewModel.onQueryChange("")
                            },
                        )
                    } else {
                        Text(
                            text = "Lịch sử học tập",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    if (!showSearch) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceVariant)
                                .clickable { showSearch = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Tìm kiếm",
                                tint = OnSurface,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardSurface),
            )
        },
        bottomBar = {
            HistoryBottomBar(
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
        ) {
            // ── Filter chips ──────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HistoryFilter.entries.forEach { filter ->
                        HistoryFilterChip(
                            filter = filter,
                            selected = uiState.selectedFilter == filter,
                            onClick = { viewModel.setFilter(filter) },
                        )
                    }
                }
            }

            // ── Section title ─────────────────────────────────────────────
            item {
                Text(
                    text = "Khóa học gần đây",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp, bottom = 12.dp),
                )
            }

            // ── Content ───────────────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    item { BLLoadingIndicator(modifier = Modifier.height(300.dp)) }
                }

                uiState.displayed.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Icon(
                                    Icons.Filled.History,
                                    contentDescription = null,
                                    tint = OnSurfaceMuted,
                                    modifier = Modifier.size(56.dp),
                                )
                                Text(
                                    text = "Chưa có lịch sử học tập",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                                )
                            }
                        }
                    }
                }

                else -> {
                    items(uiState.displayed, key = { it.id }) { course ->
                        HistoryCourseCard(
                            course = course,
                            onContinue = { onNavigateToCourse(course.id) },
                            onViewCertificate = onNavigateToCertificates,
                            onReview = { onNavigateToCourse(course.id) },
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Filter Chip
// ─────────────────────────────────────────────
@Composable
private fun HistoryFilterChip(
    filter: HistoryFilter,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(CircleShape)
            .background(if (selected) Primary else CardSurface)
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = filter.label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = if (selected) Color.White else OnSurfaceVariant,
                fontSize = 13.sp,
            ),
        )
    }
}

// ─────────────────────────────────────────────
//  Course History Card
// ─────────────────────────────────────────────
@Composable
private fun HistoryCourseCard(
    course: Course,
    onContinue: () -> Unit,
    onViewCertificate: () -> Unit,
    onReview: () -> Unit,
) {
    val isCompleted = course.progress >= 1f
    val progressAnim by animateFloatAsState(
        targetValue = course.progress,
        label = "progress_${course.id}",
    )
    val progressColor = if (isCompleted) Success else Primary
    val startDate = "15/10/2023" // placeholder — real data would come from API

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // ── Top row: thumbnail + info ─────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant),
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "Bắt đầu: $startDate",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    // Status badge + percentage
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (isCompleted) {
                            CompletedBadge()
                        } else {
                            InProgressBadge()
                        }
                        Text(
                            text = "${(course.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = progressColor,
                            ),
                        )
                    }
                }
            }

            // ── Progress bar ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariant),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnim)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(progressColor),
                )
            }

            // ── Action buttons ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isCompleted) Arrangement.End else Arrangement.End,
            ) {
                if (isCompleted) {
                    // View certificate
                    OutlinedButton(
                        onClick = onViewCertificate,
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp),
                    ) {
                        Text(
                            text = "Xem chứng chỉ",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    // Review
                    Button(
                        onClick = onReview,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp),
                    ) {
                        Text(
                            text = "Học lại",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = OnSurface,
                            ),
                        )
                    }
                } else {
                    // Continue
                    Button(
                        onClick = onContinue,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp),
                    ) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Tiếp tục học",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                            ),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Status Badges
// ─────────────────────────────────────────────
@Composable
private fun InProgressBadge() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFFDBEAFE))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = "Đang học",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF1D4ED8),
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun CompletedBadge() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFFDCFCE7))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = "Đã hoàn thành",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF15803D),
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

// ─────────────────────────────────────────────
//  Inline Search Field (shown in TopAppBar)
// ─────────────────────────────────────────────
@Composable
private fun HistorySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant)
            .padding(horizontal = 12.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = OnSurfaceMuted,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Tìm khóa học...",
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
        IconButton(
            onClick = if (query.isNotEmpty()) ({ onQueryChange("") }) else onClose,
            modifier = Modifier.size(20.dp),
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Đóng",
                tint = OnSurfaceMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Bottom Navigation Bar
// ─────────────────────────────────────────────
@Composable
private fun HistoryBottomBar(
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
            icon = { Icon(Icons.Filled.History, contentDescription = null) },
            label = { Text("Lịch sử", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Hồ sơ", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
    }
}
