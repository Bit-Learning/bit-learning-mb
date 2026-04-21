
/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.splash.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.bitlearning.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "logo_scale",
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2200)
        // TODO: Check stored auth token to decide route
        // For now, navigate to auth
        onNavigateToAuth()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo_text),
                contentDescription = "App logo",
                modifier = Modifier
                    .scale(scale)
                    .height(80.dp)
            )

            Text(
                text = "Làm chủ kỹ năng mọi lúc, mọi nơi",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                ),
                modifier = Modifier.scale(scale),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp,
            )
        }
    }
}