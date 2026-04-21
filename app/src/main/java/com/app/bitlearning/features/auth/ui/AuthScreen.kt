/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.auth.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.*
import com.app.bitlearning.core.common.theme.*
import com.app.bitlearning.R

data class OnboardingPage(
    val image: Any,
    val title: String,
    val description: String,
)

private val onboardingPages = listOf(
    OnboardingPage(
        image = R.drawable.onboarding_2,
        title = "Làm chủ kỹ năng\nmới mọi lúc, mọi nơi",
        description = "Tiếp cận hàng ngàn khóa học từ chuyên gia về công nghệ, thiết kế, kinh doanh và hơn thế nữa. Học theo tốc độ của riêng bạn với những giảng viên hàng đầu thế giới.",
    ),
    OnboardingPage(
        image = R.drawable.onboarding_1,
        title = "Học từ chuyên gia\nhàng đầu thế giới",
        description = "Các khóa học được thiết kế bởi những chuyên gia thực tế. Nội dung cập nhật liên tục, phù hợp với nhu cầu thực tiễn của thị trường.",
    ),
    OnboardingPage(
        image = R.drawable.onboarding_3,
        title = "Nhận chứng chỉ\ncó giá trị thực tế",
        description = "Hoàn thành khóa học và nhận chứng chỉ được công nhận rộng rãi. Nâng cao hồ sơ xin việc và khẳng định năng lực của bạn.",
    ),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAuthDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })

    LaunchedEffect(uiState.authSuccess) {
        if (uiState.authSuccess) onAuthSuccess()
    }

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
                TextButton(onClick = { showAuthDialog = true }) {
                    Text(
                        text = "Bỏ qua",
                        style = MaterialTheme.typography.titleMedium.copy(color = Primary),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Primary,
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingPageContent(page = onboardingPages[page])
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
                    repeat(onboardingPages.size) { index ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (pagerState.currentPage == index) {
                                        Primary
                                    } else {
                                        Primary.copy(alpha = 0.25f)
                                    },
                                )
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
                    text = "Bắt đầu ngay →",
                    onClick = { showAuthDialog = true },
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Đã có tài khoản? ",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TextButton(
                        onClick = { showAuthDialog = true },
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = "Đăng nhập",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Primary,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                }
            }
        }
    }

    // Auth Dialog
    if (showAuthDialog) {
        AuthDialog(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { showAuthDialog = false },
        )
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Image card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
        ) {
            AsyncImage(
                model = page.image,
                contentDescription = null,
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
                textAlign = TextAlign.Center,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Auth Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AuthDialog(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    onDismiss: () -> Unit,
) {
    var showPassword by remember { mutableStateOf(false) }

    // ── Google Sign-In launcher ─────────────────────────────────────────────

    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

    // Collect the one-shot intent event emitted by the ViewModel.
    LaunchedEffect(Unit) {
        viewModel.googleSignInEvent.collect { intent ->
            googleLauncher.launch(intent)
        }
    }

    // ── Dialog UI ─────────────────────────────────────────────────────────────
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "App logo",
                                modifier = Modifier.size(40.dp),
                            )
                        }
                        Text(
                            text = "Xác thực tài khoản",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, null, tint = OnSurfaceVariant)
                        }
                    }
                }

                // Tabs
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(0.dp),
//                ) {
//                    TabButton(
//                        text = "Đăng nhập",
//                        selected = uiState.isLoginMode,
//                        modifier = Modifier.weight(1f),
//                        onClick = { if (!uiState.isLoginMode) viewModel.switchMode() },
//                    )
//                    TabButton(
//                        text = "Đăng ký",
//                        selected = !uiState.isLoginMode,
//                        modifier = Modifier.weight(1f),
//                        onClick = { if (uiState.isLoginMode) viewModel.switchMode() },
//                    )
//                }

                BLDivider()

                // Register success banner (real mode only)
                if (uiState.registerMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = uiState.registerMessage,
                                style = MaterialTheme.typography.bodySmall.copy(color = Primary),
                            )
                        }
                    }
                }

                // Fields
                if (!uiState.isLoginMode) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Họ và tên", style = MaterialTheme.typography.titleMedium)
                        BLTextField(
                            value = uiState.name,
                            onValueChange = viewModel::onNameChange,
                            placeholder = "Nguyễn Văn A",
                            leadingIcon = Icons.Filled.Person,
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Địa chỉ Email", style = MaterialTheme.typography.titleMedium)
                    BLTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        placeholder = "example@gmail.com",
                        leadingIcon = Icons.Filled.Email,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Mật khẩu", style = MaterialTheme.typography.titleMedium)
//                        if (uiState.isLoginMode) {
//                            TextButton(
//                                onClick = { /* TODO: Forgot password */ },
//                                contentPadding = PaddingValues(0.dp),
//                            ) {
//                                Text(
//                                    "Quên mật khẩu?",
//                                    style = MaterialTheme.typography.bodySmall.copy(color = Primary),
//                                )
//                            }
//                        }
                    }
                    BLTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        placeholder = "Nhập mật khẩu của bạn",
                        leadingIcon = Icons.Filled.Lock,
                        isPassword = !showPassword,
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) {
                                        Icons.Filled.VisibilityOff
                                    } else {
                                        Icons.Filled.Visibility
                                    },
                                    contentDescription = null,
                                    tint = OnSurfaceMuted,
                                )
                            }
                        },
                    )
                }

                // Error
                if (uiState.error != null) {
                    Text(
                        text = uiState.error,
                        style = MaterialTheme.typography.bodySmall.copy(color = Error),
                    )
                }

                // CTA
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else {
                    BLPrimaryButton(
                        text = if (uiState.isLoginMode) {
                            "Đăng nhập vào tài khoản →"
                        } else {
                            "Tạo tài khoản →"
                        },
                        onClick = {
                            if (uiState.isLoginMode) viewModel.login() else viewModel.register()
                        },
                    )
                }

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    BLDivider(Modifier.weight(1f))
                    Text(
                        "HOẶC TIẾP TỤC VỚI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OnSurfaceMuted,
                            letterSpacing = 0.8.sp,
                        ),
                    )
                    BLDivider(Modifier.weight(1f))
                }

                // Social login buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SocialLoginButton(
                        text = "Google",
                        onClick = { viewModel.initiateGoogleLogin() },
                        modifier = Modifier.weight(1f),
                    )
//                    SocialLoginButton(
//                        text = "GitHub",
//                        onClick = { viewModel.initiateGitHubLogin() },
//                        modifier = Modifier.weight(1f),
//                    )
                }

                // Terms
                Text(
                    text = "Bằng cách đăng nhập, bạn đồng ý với Điều khoản dịch vụ và Chính sách bảo mật của chúng tôi.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        textAlign = TextAlign.Center,
                        color = OnSurfaceMuted,
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Supporting composables
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Small chip that shows the current mode (Mock / API) and lets the user toggle it.
 * Tapping it switches between local mock data and the real backend.
 */
@Composable
private fun MockToggleChip(
    useMock: Boolean,
    onToggle: () -> Unit,
) {
    FilterChip(
        selected = useMock,
        onClick = onToggle,
        label = {
            Text(
                text = if (useMock) "Mock" else "API",
                style = MaterialTheme.typography.labelSmall,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = if (useMock) Icons.Filled.BugReport else Icons.Filled.Wifi,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Primary.copy(alpha = 0.15f),
            selectedLabelColor = Primary,
            selectedLeadingIconColor = Primary,
        ),
    )
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (selected) Primary else OnSurfaceMuted,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                ),
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(2.dp)
                    .background(Primary, RoundedCornerShape(1.dp)),
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Divider),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "$text logo",
                modifier = Modifier.size(30.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, style = MaterialTheme.typography.titleSmall)
        }
    }
}
