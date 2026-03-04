/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.payment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSettingScreen(
    onNavigateBack: () -> Unit,
    viewModel: PaymentSettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cards = state.methods.filter { it.type == PaymentMethodType.CARD }
    val wallets = state.methods.filter { it.type == PaymentMethodType.EWALLET }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Phương thức thanh toán",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                // Transparent action to balance the title
                actions = {
                    IconButton(onClick = {}, enabled = false) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.Transparent,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
            )
        },
        bottomBar = {
            Surface(
                color = CardSurface,
                shadowElevation = 8.dp,
                tonalElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { viewModel.saveChanges(onNavigateBack) },
                        enabled = !state.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        elevation = ButtonDefaults.buttonElevation(2.dp),
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Lưu thay đổi",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                ),
                            )
                        }
                    }
                    Text(
                        text = "PCI DSS Compliant & Secure Encryption".uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceMuted,
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
        ) {
            // ── Security banner ───────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryContainer.copy(alpha = 0.6f))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        Icons.Filled.VerifiedUser,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "Thanh toán an toàn",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Primary,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                        Text(
                            text = "Thông tin thanh toán của bạn được mã hóa và bảo mật theo tiêu chuẩn quốc tế.",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant),
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── Cards section title ───────────────────────────────────────
            item {
                Text(
                    text = "Thẻ tín dụng & Ghi nợ",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }

            // ── Card rows ────────────────────────────────────────────────
            items(cards, key = { it.id }) { method ->
                PaymentMethodRow(
                    method = method,
                    isSelected = state.selectedId == method.id,
                    onSelect = { viewModel.selectMethod(method.id) },
                    onConnect = { viewModel.connectWallet(method.id) },
                )
                Spacer(Modifier.height(10.dp))
            }

            // ── E-wallets section title ───────────────────────────────────
            item {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Ví điện tử",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }

            // ── Wallet rows ───────────────────────────────────────────────
            items(wallets, key = { it.id }) { method ->
                PaymentMethodRow(
                    method = method,
                    isSelected = state.selectedId == method.id,
                    onSelect = { if (method.isConnected) viewModel.selectMethod(method.id) },
                    onConnect = { viewModel.connectWallet(method.id) },
                )
                Spacer(Modifier.height(10.dp))
            }

            // ── Add new method ────────────────────────────────────────────
            item {
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 2.dp,
                            brush = SolidColor(Divider),
                            shape = RoundedCornerShape(14.dp),
                        )
                        .clickable { }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Filled.AddCircle,
                            contentDescription = null,
                            tint = OnSurfaceMuted,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = "Thêm phương thức mới",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = OnSurfaceMuted,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Single payment method row card
// ─────────────────────────────────────────────
@Composable
private fun PaymentMethodRow(
    method: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onConnect: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .width(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (method.logoUrl != null) {
                    AsyncImage(
                        model = method.logoUrl,
                        contentDescription = method.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(6.dp),
                    )
                } else {
                    Icon(
                        Icons.Filled.CreditCard,
                        contentDescription = null,
                        tint = OnSurfaceMuted,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            // Name + detail
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = method.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                )
                Text(
                    text = method.detail,
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                )
            }

            // Right-side action
            if (!method.isConnected) {
                TextButton(
                    onClick = onConnect,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "Kết nối",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (method.isDefault) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryContainer)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = "Mặc định",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                ),
                            )
                        }
                    }
                    RadioButton(
                        selected = isSelected,
                        onClick = onSelect,
                        modifier = Modifier.size(24.dp),
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Primary,
                            unselectedColor = OnSurfaceMuted,
                        ),
                    )
                }
            }
        }
    }
}
