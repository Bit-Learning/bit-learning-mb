/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bitlearning.core.common.components.*
import com.app.bitlearning.core.common.theme.*
import com.app.bitlearning.domain.model.Lecture
import com.app.bitlearning.features.player.components.BLVideoPlayer

@Composable
fun PlayerScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf("Bài học", "Giới thiệu", "Tài liệu")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        PlayerTopBar(
            title = uiState.course?.title ?: "",
            onBack = onNavigateBack,
        )

        BLVideoPlayer(
            videoUrl = uiState.currentVideoUrl,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
        )

        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = Surface,
            contentColor = Primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    color = Primary,
                )
            },
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (uiState.selectedTab == index) {
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

        when (uiState.selectedTab) {
            0 -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "TIẾN ĐỘ KHÓA HỌC",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceMuted,
                            letterSpacing = 0.8f.sp,
                        ),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        val progress = uiState.course?.progress ?: 0f
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium.copy(color = Primary),
                        )
                        TextButton(
                            onClick = { viewModel.toggleAutoPlay() },
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Autorenew,
                                contentDescription = null,
                                tint = if (uiState.isAutoPlay) Primary else OnSurfaceMuted,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = "TỰ ĐỘNG PHÁT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (uiState.isAutoPlay) Primary else OnSurfaceMuted,
                                ),
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    BLProgressBar(
                        progress = uiState.course?.progress ?: 0f,
                        showLabel = false,
                    )
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.lessons) { lecture ->
                        PlayerLectureItem(
                            lecture = lecture,
                            isCurrentLecture = lecture.id == uiState.currentLesson?.id,
                            onClick = { viewModel.selectLesson(lecture) },
                        )
                        BLDivider(Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
            1 -> {
                Text(
                    text = uiState.course?.description ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(20.dp),
                )
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Chưa có tài liệu",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        Box(modifier = Modifier.padding(16.dp)) {
            BLPrimaryButton(
                text = "  Đánh dấu đã hoàn thành",
                onClick = { viewModel.markCurrentCompleted() },
                leadingIcon = Icons.Filled.CheckCircle,
            )
        }
    }
}


@Composable
private fun PlayerLectureItem(
    lecture: Lecture,
    isCurrentLecture: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isCurrentLecture) PrimaryContainer.copy(alpha = 0.4f) else Background)
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
                        isCurrentLecture -> Primary
                        lecture.isCompleted -> PrimaryContainer
                        lecture.isLocked -> SurfaceVariant
                        else -> SurfaceVariant
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            when {
                lecture.isCompleted && !isCurrentLecture -> Icon(
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
                    tint = if (isCurrentLecture) OnPrimary else OnSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${lecture.orderIndex.toString().padStart(2, '0')}. ${lecture.title}",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = when {
                        lecture.isLocked -> OnSurfaceMuted
                        isCurrentLecture -> Primary
                        else -> OnSurface
                    },
                    fontWeight = if (isCurrentLecture) FontWeight.SemiBold else FontWeight.Normal,
                    textDecoration = if (lecture.isCompleted && !isCurrentLecture) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = lecture.type.name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isCurrentLecture) Primary else OnSurfaceMuted,
                    ),
                )
                if (isCurrentLecture) {
                    Text(
                        text = "• Đang phát",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
        }

        if (isCurrentLecture) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Primary),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
            }
        },
        actions = {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Filled.MoreVert, null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
    )
}

private fun Modifier.tabIndicatorOffset(tabPosition: TabPosition): Modifier = this.fillMaxWidth(1f / 3)
    .wrapContentSize(Alignment.BottomStart)
    .offset(x = tabPosition.left)

private val Float.sp get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)
