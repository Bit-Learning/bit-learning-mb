/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.courses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bitlearning.core.common.components.BLBottomNavBar
import com.app.bitlearning.core.common.components.BLCourseCard
import com.app.bitlearning.core.common.components.BLLoadingIndicator
import com.app.bitlearning.core.common.components.BLSectionHeader
import com.app.bitlearning.core.common.theme.Background
import com.app.bitlearning.core.common.theme.OnSurfaceMuted

@Composable
fun CoursesScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: CoursesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = { CoursesTopBar() },
        bottomBar = {
            BLBottomNavBar(
                currentRoute = "courses",
                onNavigate = { route ->
                    when (route) {
                        "home" -> onNavigateToHome()
                        "courses" -> Unit // already here
                        "profile" -> onNavigateToProfile()
                        "search" -> onNavigateToSearch()
                    }
                },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                BLLoadingIndicator(modifier = Modifier.padding(padding))
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = uiState.error ?: "Đã xảy ra lỗi",
                        style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                    )
                }
            }

            uiState.courses.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Chưa có khóa học nào",
                        style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                    )
                }
            }

            else -> {
                CoursesContent(
                    state = uiState,
                    onCourseClick = onNavigateToCourse,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoursesTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Khóa học",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
    )
}

@Composable
private fun CoursesContent(
    state: CoursesUiState,
    onCourseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            BLSectionHeader(
                title = "Tất cả khóa học",
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                state.courses.chunked(2).forEach { row ->
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
                        if (row.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
