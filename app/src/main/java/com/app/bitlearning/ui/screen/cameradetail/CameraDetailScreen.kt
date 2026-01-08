/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.ui.screen.cameradetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.bitlearning.models.Camera
import com.app.bitlearning.models.CameraType
import com.app.bitlearning.models.CurrencyWithSymbol
import com.app.bitlearning.utils.CurrencyManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraDetailScreen(
    cameraId: Int,
    onBackClick: () -> Unit,
    currencyManager: CurrencyManager? = null,
    viewModel: CameraDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(cameraId) {
        viewModel.loadCamera(cameraId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.camera?.name ?: "Camera Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Add to favorites",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(onClick = { /* TODO: Share functionality */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Error loading camera details",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = uiState.error ?: "An unexpected error occurred.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadCamera(cameraId) },
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.camera != null -> {
                CameraDetailContent(
                    camera = uiState.camera!!,
                    currencyManager = currencyManager,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
private fun CameraDetailContent(
    camera: Camera,
    currencyManager: CurrencyManager?,
    modifier: Modifier = Modifier,
) {
    val convertedPrice by remember(camera.price) {
        derivedStateOf {
            currencyManager?.convertPrice(camera.price, "en")
                ?: "${camera.currencyWithSymbol}${camera.price}"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Hero Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        ) {
            AsyncImage(
                model = camera.icon,
                contentDescription = camera.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                            ),
                            startY = 200f,
                            endY = Float.POSITIVE_INFINITY,
                        ),
                    ),
            )

            // Type badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
            ) {
                Text(
                    text = camera.type.name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        // Content
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            // Title and Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = camera.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < 4) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.3f),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        Text(
                            text = "4.${(1..9).random()} (${(50..500).random()} reviews)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Price
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                    Text(
                        text = convertedPrice,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = camera.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 24.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Specifications
            Text(
                text = "Specifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    SpecificationRow(
                        label = "Type",
                        value = camera.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    )
                    VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SpecificationRow(
                        label = "Status",
                        value = if (camera.isEnabled) "Available" else "Out of Stock",
                    )
                    VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SpecificationRow(
                        label = "Visibility",
                        value = if (camera.isVisible) "Listed" else "Unlisted",
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = { /* TODO: Add to cart */ },
                    modifier = Modifier.weight(1f),
                    enabled = camera.isEnabled,
                ) {
                    Text("Add to Cart")
                }

                OutlinedButton(
                    onClick = { /* TODO: Buy now */ },
                    modifier = Modifier.weight(1f),
                    enabled = camera.isEnabled,
                ) {
                    Text("Buy Now")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SpecificationRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraDetailScreenPreview() {
    MaterialTheme {
        CameraDetailContent(
            camera = Camera(
                id = 1,
                name = "Canon EOS R5",
                type = CameraType.MIRRORLESS,
                price = 3899.99,
                currencyWithSymbol = CurrencyWithSymbol.USD,
                icon = "https://images.unsplash.com/photo-1606983340126-99ab4feaa64a?w=400",
                description = "Professional full-frame mirrorless camera with 45MP resolution, 8K video recording, and advanced autofocus system. Perfect for professional photography and videography.",
                isEnabled = true,
                isVisible = true,
            ),
            currencyManager = null,
        )
    }
}
