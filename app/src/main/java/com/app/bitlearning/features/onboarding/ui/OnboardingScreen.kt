/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 */
package com.app.bitlearning.features.onboarding.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.BLLoadingIndicator
import com.app.bitlearning.core.common.components.BLPrimaryButton
import com.app.bitlearning.core.common.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    if (uiState.isLoading) {
        BLLoadingIndicator()
        return
    }

    val pages = uiState.pages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Skip button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                TextButton(onClick = {
                    viewModel.completeOnboarding()
                    onComplete()
                }) {
                    Text(
                        text = "Bỏ qua",
                        style = MaterialTheme.typography.titleMedium.copy(color = Primary),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { pageIndex ->
                val page = pages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
                    ) {
                        AsyncImage(
                            model = page.imageUrl,
                            contentDescription = page.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                        ),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = OnSurfaceVariant,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Dots + CTA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // Page dots
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(pages.size) { index ->
                        val color by animateColorAsState(
                            targetValue = if (pagerState.currentPage == index) Primary else Primary.copy(alpha = 0.25f),
                            label = "dot_color",
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                                .then(
                                    if (pagerState.currentPage == index) {
                                        Modifier.size(width = 24.dp, height = 8.dp)
                                    } else {
                                        Modifier.size(8.dp)
                                    },
                                ),
                        )
                    }
                }

                BLPrimaryButton(
                    text = if (isLastPage) "Bắt đầu ngay →" else "Tiếp tục →",
                    onClick = {
                        if (isLastPage) {
                            viewModel.completeOnboarding()
                            onComplete()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                )
            }
        }
    }
}
