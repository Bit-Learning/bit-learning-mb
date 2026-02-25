/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bitlearning.core.common.components.BLBottomNavBar
import com.app.bitlearning.core.common.components.BLCourseCard
import com.app.bitlearning.core.common.components.BLLoadingIndicator
import com.app.bitlearning.core.common.components.BLSectionHeader
import com.app.bitlearning.core.common.theme.Background
import com.app.bitlearning.core.common.theme.OnSurfaceMuted
import com.app.bitlearning.core.common.theme.SurfaceVariant

@Composable
fun SearchScreen(
    onNavigateToCourse: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
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
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background),
        ) {
            SearchBar(
                initialQuery = uiState.query,
                onSearch = { query -> viewModel.search(query) },
            )

            Spacer(Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    BLLoadingIndicator()
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = uiState.error ?: "Đã xảy ra lỗi",
                            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                        )
                    }
                }

                uiState.results.isEmpty() && uiState.query.isNotBlank() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Không tìm thấy khóa học phù hợp",
                            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                        )
                    }
                }

                uiState.results.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Nhập từ khóa để tìm kiếm khóa học",
                            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                        )
                    }
                }

                else -> {
                    SearchResults(
                        state = uiState,
                        onCourseClick = onNavigateToCourse,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    initialQuery: String,
    onSearch: (String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf(initialQuery) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Tìm kiếm khóa học, kỹ năng...",
                        color = OnSurfaceMuted,
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch(query) },
                ),
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Xóa",
                        tint = OnSurfaceMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    state: SearchUiState,
    onCourseClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item {
            BLSectionHeader(
                title = "Kết quả tìm kiếm",
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
                state.results.chunked(2).forEach { row ->
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
