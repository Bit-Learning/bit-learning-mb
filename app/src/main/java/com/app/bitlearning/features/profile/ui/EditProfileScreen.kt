/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 */
package com.app.bitlearning.features.profile.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.*
import com.app.bitlearning.core.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    // Avatar picker
    val avatarLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            if (bytes != null) {
                viewModel.uploadAvatar(bytes, "avatar.jpg")
            }
        }
    }

    // Background picker
    val backgroundLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            if (bytes != null) {
                viewModel.uploadBackground(bytes, "background.jpg")
            }
        }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cập nhật hồ sơ",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
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
            Surface(color = Surface, tonalElevation = 0.dp, shadowElevation = 8.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                ) {
                    BLPrimaryButton(
                        text = "Lưu thay đổi",
                        onClick = { viewModel.saveProfile(onNavigateBack) },
                        enabled = !uiState.isSaving,
                        leadingIcon = if (uiState.isSaving) null else Icons.Filled.CheckCircle,
                    )
                }
            }
        },
    ) { padding ->
        if (uiState.isLoading) {
            BLLoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Background Image + Avatar Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                ) {
                    // Background image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                            .clickable { backgroundLauncher.launch("image/*") },
                    ) {
                        if (uiState.backgroundImageUrl != null) {
                            AsyncImage(
                                model = uiState.backgroundImageUrl,
                                contentDescription = "Ảnh bìa",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(listOf(Primary, PrimaryLight)),
                                    ),
                            )
                        }
                        // Overlay edit icon
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (uiState.isUploadingBackground) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Icon(
                                        Icons.Filled.PhotoCamera,
                                        contentDescription = "Thay đổi ảnh bìa",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Text(
                                        "Thay đổi ảnh bìa",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                    )
                                }
                            }
                        }
                    }

                    // Avatar overlapping background
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 0.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable { avatarLauncher.launch("image/*") },
                        ) {
                            BLAvatar(
                                imageUrl = uiState.avatarUrl,
                                size = 120.dp,
                                showBadge = false,
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (uiState.isUploadingAvatar) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.PhotoCamera,
                                        contentDescription = "Thay đổi ảnh",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Form Fields
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    EditProfileField(
                        label = "Họ và tên",
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        placeholder = "Nhập họ và tên",
                        leadingIcon = Icons.Filled.Person,
                    )

                    EditProfileField(
                        label = "Email",
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        placeholder = "Địa chỉ email",
                        leadingIcon = Icons.Filled.Email,
                    )

                    EditProfileField(
                        label = "Số điện thoại",
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChange,
                        placeholder = "Nhập số điện thoại",
                        leadingIcon = Icons.Filled.Phone,
                    )

                    EditProfileField(
                        label = "Ngày sinh",
                        value = uiState.birthDate,
                        onValueChange = viewModel::onBirthDateChange,
                        placeholder = "DD/MM/YYYY",
                        leadingIcon = Icons.Filled.CalendarMonth,
                    )

                    // Bio
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Giới thiệu bản thân",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurfaceVariant,
                            ),
                        )
                        OutlinedTextField(
                            value = uiState.bio,
                            onValueChange = viewModel::onBioChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Viết một chút về bản thân bạn...",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = OnSurfaceMuted),
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Description,
                                    contentDescription = null,
                                    tint = OnSurfaceMuted,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .offset(y = (-36).dp),
                                )
                            },
                            minLines = 4,
                            maxLines = 6,
                            singleLine = false,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Divider,
                                focusedBorderColor = Primary,
                                unfocusedContainerColor = SurfaceVariant,
                                focusedContainerColor = Surface,
                            ),
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = OnSurfaceVariant,
            ),
        )
        BLTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
        )
    }
}
