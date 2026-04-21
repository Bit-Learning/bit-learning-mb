/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.courses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.BLBottomNavBar
import com.app.bitlearning.core.common.components.BLLoadingIndicator
import com.app.bitlearning.core.common.theme.*
import com.app.bitlearning.domain.model.Course
import com.app.bitlearning.domain.model.CourseCategory
import androidx.core.graphics.toColorInt

@Composable
fun CoursesScreen(
    onNavigateToCourse: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotification: () -> Unit = {},
    onLaunchPythonCompiler: () -> Unit = {},
    viewModel: CoursesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val availableCategories = CourseCategory.entries
    val groupedCourses = uiState.filteredCourses
        .groupBy { it.grade }
        .toList()
        .sortedWith(compareBy { it.first ?: Int.MAX_VALUE })

    Scaffold(
        containerColor = Background,
        bottomBar = {
            BLBottomNavBar(
                currentRoute = "courses",
                onNavigate = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "courses" -> Unit
                        "profile" -> onNavigateToProfile()
                        "search" -> onNavigateToSearch()
                    }
                },
                onLaunchPythonCompiler = onLaunchPythonCompiler,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // ── Sticky header with filter chips ──────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background),
                ) {
                    // Title row
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp, bottom = 12.dp)
                    ) {
                        Text(
                            text = "Danh sách khóa học",
                            style = MaterialTheme.typography.headlineMedium,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Tất cả khóa học trên hệ thống",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceMuted // hoặc Color.Gray / MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (availableCategories.size > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FilterChip(
                                label = "Tất cả",
                                selected = uiState.selectedCategory == null,
                                onClick = { viewModel.setCategory(null) },
                            )
                            availableCategories.forEach { category ->
                                FilterChip(
                                    label = category.displayName,
                                    selected = uiState.selectedCategory == category,
                                    onClick = { viewModel.setCategory(category) },
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Divider, thickness = 1.dp)
                }
            }

            // ── Content ───────────────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    item {
                        BLLoadingIndicator(modifier = Modifier.height(300.dp))
                    }
                }

                uiState.error != null -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = uiState.error ?: "Đã xảy ra lỗi",
                                style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                            )
                        }
                    }
                }

                uiState.filteredCourses.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Chưa có khóa học nào",
                                style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                            )
                        }
                    }
                }

                else -> {
                    item { Spacer(Modifier.height(12.dp)) }
                    groupedCourses.forEach { (grade, courses) ->
                        item {
                            Text(
                                text = if (grade != null) "Lớp $grade" else "Khác",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 8.dp, bottom = 6.dp),
                            )
                        }
                        items(courses, key = { it.id }) { course ->
                            CourseListCard(
                                course = course,
                                onClick = { onNavigateToCourse(course.id) },
                            )
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Filter Chip
// ─────────────────────────────────────────────
@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) Primary else CardSurface)
            .clickable { onClick() }
            .then(
                if (!selected) {
                    Modifier
                        .clip(RoundedCornerShape(50.dp))
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 18.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = if (selected) Color.White else OnSurfaceVariant,
                fontSize = 13.sp,
            ),
        )
    }
}

// ─────────────────────────────────────────────
//  Course List Card (horizontal)
// ─────────────────────────────────────────────
@Composable
private fun CourseListCard(
    course: Course,
    onClick: () -> Unit,
) {
    val categoryColor = Color(course.category.colorHex.toColorInt())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Thumbnail
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = course.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(140.dp)
                    .fillMaxHeight()
                    .background(SurfaceVariant),
            )

            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Category + Rating row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
//                    Box(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(4.dp))
//                            .background(categoryColor.copy(alpha = 0.12f))
//                            .padding(horizontal = 8.dp, vertical = 3.dp),
//                    ) {
//                        Text(
//                            text = course.category.displayName.uppercase(),
//                            style = MaterialTheme.typography.labelSmall.copy(
//                                color = categoryColor,
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 9.sp,
//                                letterSpacing = 0.8.sp,
//                            ),
//                        )
//                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Warning,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = course.rating.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                            ),
                        )
                    }
                }

                // Title
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                // Instructor
                Text(
                    text = "Giảng viên: ${course.instructor}",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                )

                Spacer(Modifier.weight(1f))

                // Lesson count + bookmark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${course.totalLectures} bài học",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BookmarkBorder,
                            contentDescription = "Lưu",
                            tint = Primary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}
