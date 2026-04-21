/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.*
import com.app.bitlearning.core.common.theme.*
import com.app.bitlearning.domain.model.Course

@Composable
fun SearchScreen(
    onNavigateToCourse: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLaunchPythonCompiler: () -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        bottomBar = {
            BLBottomNavBar(
                currentRoute = "search",
                onNavigate = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "courses" -> onNavigateToCourses()
                        "profile" -> onNavigateToProfile()
                        "search" -> Unit
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
            // ── Sticky-style header + search bar ──────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background)
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp, bottom = 12.dp),
                ) {
                    // Title row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            IconButton(
                                onClick = onNavigateToHome,
                                modifier = Modifier.size(36.dp),
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Quay lại",
                                    tint = Primary,
                                )
                            }
                            Text(
                                text = "Tìm kiếm",
                                style = MaterialTheme.typography.displayMedium,
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Search bar
                    SearchInputBar(
                        query = uiState.query,
                        onQueryChange = viewModel::onQueryChange,
                    )
                }
            }

            // ── Content: either results or default explore view ────────────
            if (uiState.query.isNotBlank()) {
                // Search results header
                item {
                    Spacer(Modifier.height(8.dp))
                    BLSectionHeader(
                        title = "Kết quả tìm kiếm",
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                }

                when {
                    uiState.isLoading -> {
                        item { BLLoadingIndicator(modifier = Modifier.height(200.dp)) }
                    }

                    uiState.error != null -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = uiState.error ?: "Đã xảy ra lỗi",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                                )
                            }
                        }
                    }

                    uiState.results.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Không tìm thấy khóa học phù hợp",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                                )
                            }
                        }
                    }

                    else -> {
                        items(uiState.results) { course ->
                            TrendingCourseRow(
                                course = course,
                                onClick = { onNavigateToCourse(course.id) },
                            )
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            } else {
                // ── Recent searches ───────────────────────────────────────
                if (uiState.recentSearches.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 8.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Tìm kiếm gần đây",
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                text = "Xóa tất cả",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Primary,
                                    fontWeight = FontWeight.Medium,
                                ),
                                modifier = Modifier.clickable { viewModel.clearRecentSearches() },
                            )
                        }
                    }

                    items(uiState.recentSearches) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onQueryChange(item) }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = null,
                                    tint = OnSurfaceMuted,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant),
                                )
                            }
                            IconButton(
                                onClick = { viewModel.removeRecentSearch(item) },
                                modifier = Modifier.size(24.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Xóa",
                                    tint = OnSurfaceMuted,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }

                // ── Popular categories ────────────────────────────────────
                item {
                    Text(
                        text = "Chủ đề lập trình phổ biến",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    PopularCategoriesGrid(
                        categories = popularCategories,
                        onCategoryClick = { viewModel.searchByCategory(it) },
                    )
                    Spacer(Modifier.height(24.dp))
                }

                // ── Trending courses ──────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Khóa học thịnh hành",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = "Xem tất cả",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Primary,
                                fontWeight = FontWeight.Medium,
                            ),
                            modifier = Modifier.clickable { onNavigateToCourses() },
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                items(uiState.trendingCourses) { course ->
                    TrendingCourseRow(
                        course = course,
                        onClick = { onNavigateToCourse(course.id) },
                    )
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Search Input Bar
// ─────────────────────────────────────────────
@Composable
private fun SearchInputBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariant)
            .padding(horizontal = 14.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = OnSurfaceMuted,
            modifier = Modifier.size(20.dp),
        )
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Tìm khóa học, kỹ năng hoặc giáo viên...",
                    style = MaterialTheme.typography.bodyLarge.copy(color = OnSurfaceMuted),
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
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Xóa",
                    tint = OnSurfaceMuted,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Popular Categories 2×2 Grid
// ─────────────────────────────────────────────
@Composable
private fun PopularCategoriesGrid(
    categories: List<PopularCategory>,
    onCategoryClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        categories.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { cat ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(PrimaryContainer)
                            .clickable { onCategoryClick(cat.label) }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = categoryIcon(cat.icon),
                            contentDescription = cat.label,
                            tint = Primary,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = cat.label,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Primary,
                            ),
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Trending Course Row Card
// ─────────────────────────────────────────────
@Composable
private fun TrendingCourseRow(
    course: Course,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Thumbnail
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = course.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant),
            )

            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
//                BLCategoryChip(
//                    label = course.category.displayName,
//                    color = categoryColor(course.category),
//                )
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Giảng viên: ${course.instructor}",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                )
                Spacer(Modifier.weight(1f))
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Warning,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = course.rating.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────
private fun categoryIcon(icon: String) = when (icon) {
    "terminal" -> Icons.Filled.Terminal
    "code" -> Icons.Filled.Code
    "data_object" -> Icons.Filled.DataObject
    "language" -> Icons.Filled.Language
    else -> Icons.Filled.Category
}

private fun categoryColor(category: com.app.bitlearning.domain.model.CourseCategory): Color = Color(android.graphics.Color.parseColor(category.colorHex))
