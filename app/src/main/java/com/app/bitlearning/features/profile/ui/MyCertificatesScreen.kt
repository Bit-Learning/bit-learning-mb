/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.profile.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.BLLoadingIndicator
import com.app.bitlearning.core.common.theme.*
import com.app.bitlearning.domain.model.Certificate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCertificatesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: MyCertificatesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        SearchField(
                            query = uiState.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChange,
                            onClose = {
                                showSearch = false
                                viewModel.onSearchQueryChange("")
                            },
                        )
                    } else {
                        Text(
                            text = "Chứng chỉ của tôi",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    if (!showSearch) {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Tìm kiếm",
                                tint = OnSurface,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
            )
        },
        bottomBar = {
            CertificatesBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToCourses = onNavigateToCourses,
                onNavigateToProfile = onNavigateToProfile,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // ── Tabs ─────────────────────────────────────────────────────
            item {
                TabRow(uiState.selectedTab, viewModel::selectTab)
            }

            // ── Section title ─────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp, bottom = 16.dp),
                ) {
                    Text(
                        text = if (uiState.selectedTab == CertificateTab.RECEIVED) {
                            "Chứng chỉ đã đạt được"
                        } else {
                            "Chứng chỉ đang chờ"
                        },
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (uiState.selectedTab == CertificateTab.RECEIVED) {
                            "Bạn đã hoàn thành ${uiState.receivedCertificates.size} khóa học"
                        } else {
                            "Theo dõi tiến độ các khóa học chưa hoàn thành"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                    )
                }
            }

            // ── Content ───────────────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    item { BLLoadingIndicator(modifier = Modifier.height(300.dp)) }
                }

                uiState.errorMessage != null -> {
                    item {
                        CertificatesErrorState(
                            message = uiState.errorMessage ?: "Không thể tải chứng chỉ",
                            onRetry = viewModel::loadCertificates,
                        )
                    }
                }

                uiState.selectedTab == CertificateTab.RECEIVED && uiState.displayedReceivedCertificates.isEmpty() -> {
                    item { EmptyState(tab = uiState.selectedTab) }
                }

                uiState.selectedTab == CertificateTab.PENDING && uiState.displayedPendingCertificates.isEmpty() -> {
                    item { EmptyState(tab = uiState.selectedTab) }
                }

                else -> {
                    if (uiState.selectedTab == CertificateTab.RECEIVED) {
                        items(uiState.displayedReceivedCertificates, key = { item ->
                            when (item) {
                                is ReceivedCertificateItem.Ready -> item.certificate.id
                                is ReceivedCertificateItem.Error -> "error-${item.courseId}"
                            }
                        }) { item ->
                            when (item) {
                                is ReceivedCertificateItem.Ready -> {
                                    val cert = item.certificate
                                    CertificateCard(
                                        certificate = cert,
                                        onShare = { shareCertificate(context, cert.localUri ?: cert.thumbnailUrl, cert.courseTitle) },
                                        onOpen = { openCertificate(context, cert.localUri ?: cert.thumbnailUrl) },
                                    )
                                }

                                is ReceivedCertificateItem.Error -> {
                                    CertificateErrorCard(
                                        title = item.courseTitle,
                                        thumbnailUrl = item.thumbnailUrl,
                                        message = item.message,
                                        onRetry = {
                                            viewModel.retryCertificate(
                                                courseId = item.courseId,
                                                courseTitle = item.courseTitle,
                                                thumbnailUrl = item.thumbnailUrl,
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.displayedPendingCertificates, key = { it.id }) { cert ->
                            CertificateCard(
                                certificate = cert,
                                onShare = { shareCertificate(context, cert.localUri ?: cert.thumbnailUrl, cert.courseTitle) },
                                onOpen = { openCertificate(context, cert.localUri ?: cert.thumbnailUrl) },
                            )
                        }
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CertificatesErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = OnSurfaceMuted,
            modifier = Modifier.size(56.dp),
        )
        Text(
            text = "Không thể tải chứng chỉ",
            style = MaterialTheme.typography.headlineSmall.copy(color = OnSurface),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
        )
        Button(onClick = onRetry) {
            Text("Thử lại")
        }
    }
}

// ─────────────────────────────────────────────
//  Tab Row
// ─────────────────────────────────────────────
@Composable
private fun TabRow(
    selectedTab: CertificateTab,
    onTabSelected: (CertificateTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        CertificateTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            val indicatorColor by animateColorAsState(
                targetValue = if (selected) Primary else Color.Transparent,
                label = "tab_indicator",
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) Primary else OnSurfaceMuted,
                label = "tab_text",
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (tab == CertificateTab.RECEIVED) "Đã nhận" else "Đang chờ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                    ),
                    modifier = Modifier.padding(vertical = 14.dp),
                )
                HorizontalDivider(
                    thickness = 2.dp,
                    color = indicatorColor,
                )
            }
        }
    }
    HorizontalDivider(color = Divider, thickness = 1.dp)
}

// ─────────────────────────────────────────────
//  Certificate Card
// ─────────────────────────────────────────────
@Composable
private fun CertificateCard(
    certificate: Certificate,
    onShare: () -> Unit,
    onOpen: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Left: info + buttons ──────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (certificate.localUri != null) PrimaryContainer else SurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = if (certificate.localUri != null) "HOÀN THÀNH" else "ĐANG HỌC",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (certificate.localUri != null) Primary else OnSurfaceMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp,
                        ),
                    )
                }

                // Title
                Text(
                    text = certificate.courseTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                // Issued date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = OnSurfaceMuted,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "Cấp ngày: ${certificate.issuedDate}",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Download
                    Button(
                        onClick = onShare,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        enabled = certificate.localUri != null,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Tải xuống",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                            ),
                        )
                    }

                    // View detail
                    Button(
                        onClick = onOpen,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        enabled = certificate.localUri != null,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = OnSurface,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            // ── Right: thumbnail ──────────────────────────────────────────
            AsyncImage(
                model = certificate.thumbnailUrl,
                contentDescription = "Chứng chỉ",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(110.dp)
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant),
            )
        }
    }
}

@Composable
private fun CertificateErrorCard(
    title: String,
    thumbnailUrl: String?,
    message: String,
    onRetry: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(SurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = "LỖI TẢI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp,
                        ),
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                )
                Button(
                    onClick = onRetry,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Tải lại",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        ),
                    )
                }
            }

            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "Khóa học đã hoàn thành",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(110.dp)
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant),
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Search Field (shown in TopAppBar)
// ─────────────────────────────────────────────
@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant)
            .padding(horizontal = 12.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = OnSurfaceMuted,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Tìm chứng chỉ...",
                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
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
            IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Filled.Clear, contentDescription = null, tint = OnSurfaceMuted, modifier = Modifier.size(16.dp))
            }
        } else {
            IconButton(onClick = onClose, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Đóng", tint = OnSurfaceMuted, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Empty State
// ─────────────────────────────────────────────
@Composable
private fun EmptyState(tab: CertificateTab) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.WorkspacePremium,
            contentDescription = null,
            tint = OnSurfaceMuted,
            modifier = Modifier.size(64.dp),
        )
        Text(
            text = if (tab == CertificateTab.RECEIVED) {
                "Bạn chưa có chứng chỉ nào"
            } else {
                "Không có chứng chỉ đang chờ"
            },
            style = MaterialTheme.typography.headlineSmall.copy(color = OnSurfaceMuted),
        )
        Text(
            text = "Hoàn thành khóa học để nhận chứng chỉ",
            style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
        )
    }
}

// ─────────────────────────────────────────────
//  Bottom Navigation Bar
// ─────────────────────────────────────────────
@Composable
private fun CertificatesBottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    NavigationBar(
        containerColor = CardSurface,
        tonalElevation = 0.dp,
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Filled.Home, contentDescription = "Trang chủ") },
            label = { Text("Trang chủ", style = MaterialTheme.typography.labelSmall) },
            colors = navBarColors(),
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToCourses,
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Khóa học") },
            label = { Text("Khóa học", style = MaterialTheme.typography.labelSmall) },
            colors = navBarColors(),
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Filled.WorkspacePremium, contentDescription = "Chứng chỉ") },
            label = { Text("Chứng chỉ", style = MaterialTheme.typography.labelSmall) },
            colors = navBarColors(),
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = "Hồ sơ") },
            label = { Text("Hồ sơ", style = MaterialTheme.typography.labelSmall) },
            colors = navBarColors(),
        )
    }
}

@Composable
private fun navBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Primary,
    selectedTextColor = Primary,
    unselectedIconColor = OnSurfaceMuted,
    unselectedTextColor = OnSurfaceMuted,
    indicatorColor = PrimaryContainer,
)

private fun openCertificate(context: Context, uriValue: String?) {
    if (uriValue.isNullOrBlank()) return
    val uri = Uri.parse(uriValue)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Mở chứng chỉ"))
}

private fun shareCertificate(context: Context, uriValue: String?, title: String) {
    if (uriValue.isNullOrBlank()) return
    val uri = Uri.parse(uriValue)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, title)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Chia sẻ chứng chỉ"))
}
