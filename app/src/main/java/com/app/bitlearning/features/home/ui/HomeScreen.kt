/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.home.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.*
import com.app.bitlearning.core.common.theme.*
import com.app.bitlearning.core.log.MainLog
import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.features.profile.ui.ProfileViewModel

@Composable
fun HomeScreen(
    onNavigateToCourse: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotification: () -> Unit = {},
    onLaunchPythonCompiler: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf("home") }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            BLBottomNavBar(
                currentRoute = currentTab,
                onNavigate = { route ->
                    currentTab = route
                    when (route) {
                        "home" -> Unit
                        "profile" -> onNavigateToProfile()
                        "courses" -> onNavigateToCourses()
                        "search" -> onNavigateToSearch()
                    }
                },
                onLaunchPythonCompiler = onLaunchPythonCompiler,
            )
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
                // Top Bar
                item {
                    HomeTopBar(
                        userName = profileUiState.user?.name?.split(" ")?.first() ?: "Bạn",
                        avatarUrl = profileUiState.user?.avatar,
                        onAvatarClick = onNavigateToProfile,
                        onNotificationClick = onNavigateToNotification,
                    )
                }

                // Search Bar
                item {
                    HomeSearchBar(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        onClick = onNavigateToSearch,
                    )
                }

                // Continue Learning Section
                if (uiState.enrolledCourses.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(12.dp))
                        BLSectionHeader(
                            title = "Tiếp tục học",
                            actionLabel = "Xem tất cả",
                            onAction = { /* TODO */ },
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    item {
                        ContinueLearningCard(
                            course = uiState.enrolledCourses.first(),
                            onClick = { onNavigateToCourse(uiState.enrolledCourses.first().id) },
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                }

                // Recommended Section
                if (uiState.recommendedCourses.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(24.dp))
                        BLSectionHeader(
                            title = "Gợi ý cho bạn",
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    item {
                        RecommendedCoursesGrid(
                            courses = uiState.recommendedCourses,
                            onCourseClick = onNavigateToCourse,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                }

                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Home Top Bar
// ─────────────────────────────────────────────
@Composable
private fun rememberGreeting(): String {
    val hour = remember {
        java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    }

    return when (hour) {
        in 5..11 -> "CHÀO BUỔI SÁNG"
        in 12..17 -> "CHÀO BUỔI CHIỀU"
        else -> "CHÀO BUỔI TỐI"
    }
}

@Composable
private fun HomeTopBar(
    userName: String,
    avatarUrl: String?,
    onAvatarClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BLAvatar(
                imageUrl = avatarUrl,
                size = 44.dp,
                modifier = Modifier.clickable { onAvatarClick() },
            )
            Column {
                Text(
                    text = rememberGreeting(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OnSurfaceMuted,
                        letterSpacing = 0.8.sp,
                    ),
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
//        IconButton(
//            onClick = onNotificationClick,
//            modifier = Modifier
//                .size(42.dp)
//                .clip(RoundedCornerShape(12.dp))
//                .background(SurfaceVariant),
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Notifications,
//                contentDescription = "Thông báo",
//                tint = OnSurface,
//            )
//        }
    }
}

// ─────────────────────────────────────────────
//  Search Bar
// ─────────────────────────────────────────────
@Composable
private fun HomeSearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = OnSurfaceMuted,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "Tìm kiếm khóa học, kỹ năng...",
                style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                maxLines = 1,
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Continue Learning Card (Hero)
// ─────────────────────────────────────────────
@Composable
private fun ContinueLearningCard(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            // Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            ) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
//                BLCategoryChip(
//                    label = course.category.displayName,
//                    color = Color(course.category.colorHex.toColorInt()),
//                )

                Text(
                    text = course.title,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "Giảng viên: ${course.instructor}",
                    style = MaterialTheme.typography.bodyMedium,
                )

                BLProgressBar(
                    progress = course.progress,
                    showLabel = true,
                    label = "Tiến độ",
                )

                BLPrimaryButton(
                    text = "▶  Học tiếp",
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Recommended Courses 2-Column Grid
// ─────────────────────────────────────────────
@Composable
private fun RecommendedCoursesGrid(
    courses: List<Course>,
    onCourseClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        courses.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { course ->
                    BLCourseCard(
                        title = course.title,
                        instructor = course.instructor,
                        rating = course.rating,
                        reviewCount = course.reviewCount,
                        imageUrl = course.thumbnailUrl,
                        modifier = Modifier.weight(1f),
                        onClick = { onCourseClick(course.id) },
                    )
                }
                // Fill remaining space if odd count
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// import for sp extension
private val Int.sp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
