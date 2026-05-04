/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.coursedetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Intent
import android.net.Uri
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.*
import com.app.bitlearning.core.common.theme.*
import com.app.bitlearning.domain.model.Lecture

@Composable
fun CourseDetailScreen(
    courseId: Int,
    onNavigateBack: () -> Unit,
    onStartLesson: (Int, Int) -> Unit,
    viewModel: CourseDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Bài học", "Giới thiệu", "Tài liệu")
    val firstAccessibleLecture = remember(uiState.lessons, uiState.hasAccess) {
        uiState.lessons.firstOrNull { uiState.hasAccess || it.isPreviewable }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            CourseDetailTopBar(
                title = uiState.course?.title ?: "",
                onBack = onNavigateBack,
            )
        },
        bottomBar = {
            if (!uiState.isLoading && uiState.course != null) {
                Box(modifier = Modifier.padding(16.dp)) {
                    if (uiState.hasAccess) {
                        BLPrimaryButton(
                            text = if (uiState.progress > 0f) "Tiếp tục học" else "Bắt đầu học",
                            onClick = {
                                firstAccessibleLecture?.let { lecture ->
                                    onStartLesson(courseId, lecture.id)
                                }
                            },
                            leadingIcon = Icons.Filled.PlayArrow,
                            enabled = firstAccessibleLecture != null,
                        )
                    } else {
                        BLPrimaryButton(
                            text = "Mua khóa học trên website",
                            onClick = {
                                val uri = Uri.parse("https://bit-learning.lch.id.vn/courses/$courseId")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            },
                            leadingIcon = Icons.Filled.ShoppingCart,
                        )
                    }
                }
            }
        },
    ) { padding ->
        if (uiState.isLoading) {
            BLLoadingIndicator()
        } else {
            val course = uiState.course ?: return@Scaffold
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                // Course Thumbnail
                item {
                    AsyncImage(
                        model = course.thumbnailUrl,
                        contentDescription = course.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(SurfaceVariant),
                    )
                }

                // Course Info
                item {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        BLCategoryChip(label = course.level.displayName)
                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.headlineLarge,
                        )
                        Text(
                            text = "Giảng viên: ${course.instructor}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BLRatingRow(rating = course.rating, reviewCount = course.reviewCount)
                            CourseInfoChip(
                                icon = Icons.Filled.PlayCircle,
                                text = "${course.totalLectures} bài",
                            )
                            CourseInfoChip(
                                icon = Icons.Filled.AccessTime,
                                text = formatTotalDuration(course.totalDuration),
                            )
                        }
                        if (uiState.progress > 0f) {
                            BLProgressBar(
                                progress = uiState.progress,
                                showLabel = true,
                                label = "TIẾN ĐỘ KHÓA HỌC",
                            )
                        }
                        Text(
                            text = if (uiState.hasAccess) "Bạn đã có quyền học khóa này" else "Chỉ xem được các bài học thử",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (uiState.hasAccess) Primary else OnSurfaceMuted,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                    BLDivider()
                }

                // Tabs
                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Surface,
                        contentColor = Primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Primary,
                            )
                        },
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = if (selectedTab == index) {
                                                FontWeight.SemiBold
                                            } else {
                                                FontWeight.Normal
                                            },
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }

                // Tab content - Lessons
                if (selectedTab == 0) {
                    items(uiState.lessons) { lecture ->
                        LectureItem(
                            lecture = lecture,
                            onClick = {
                                if (!lecture.isLocked) onStartLesson(courseId, lecture.id)
                            },
                        )
                        BLDivider(Modifier.padding(horizontal = 16.dp))
                    }
                }

                // Tab content - Description
                if (selectedTab == 1) {
                    item {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(text = course.description, style = MaterialTheme.typography.bodyLarge)
                            if (course.outcome.isNotBlank()) {
                                Text("Kết quả đạt được", style = MaterialTheme.typography.titleMedium)
                                Text(course.outcome, style = MaterialTheme.typography.bodyMedium)
                            }
                            if (course.requirement.isNotBlank()) {
                                Text("Yêu cầu", style = MaterialTheme.typography.titleMedium)
                                Text(course.requirement, style = MaterialTheme.typography.bodyMedium)
                            }
                            if (course.audience.isNotBlank()) {
                                Text("Đối tượng phù hợp", style = MaterialTheme.typography.titleMedium)
                                Text(course.audience, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}


// ─────────────────────────────────────────────
//  Lecture List Item
// ─────────────────────────────────────────────
@Composable
private fun LectureItem(
    lecture: Lecture,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !lecture.isLocked) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    when {
                        lecture.isCompleted -> PrimaryContainer
                        lecture.isLocked -> SurfaceVariant
                        else -> SurfaceVariant
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            when {
                lecture.isCompleted -> Icon(
                    Icons.Filled.CheckCircle,
                    null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp),
                )
                lecture.isLocked -> Icon(
                    Icons.Filled.Lock,
                    null,
                    tint = OnSurfaceMuted,
                    modifier = Modifier.size(18.dp),
                )
                else -> Icon(
                    Icons.Filled.PlayArrow,
                    null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${lecture.orderIndex.toString().padStart(2, '0')}. ${lecture.title}",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = if (lecture.isLocked) OnSurfaceMuted else OnSurface,
                    textDecoration = if (lecture.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                ),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = lecture.type.name,
                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDetailTopBar(
    title: String,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: More options */ }) {
                Icon(Icons.Filled.MoreVert, null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
    )
}

@Composable
private fun CourseInfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(icon, null, tint = OnSurfaceMuted, modifier = Modifier.size(14.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}

private fun formatTotalDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

private fun Modifier.tabIndicatorOffset(tabPosition: androidx.compose.material3.TabPosition): Modifier = this.then(
    Modifier.fillMaxWidth(1f / 3)
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = tabPosition.left),
)
