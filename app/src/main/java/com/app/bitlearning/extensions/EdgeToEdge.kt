/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.extensions

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Enables edge-to-edge display with a custom status bar style .
 * This sets the status bar to a light style with a semi-transparent black scrim.
 */
fun ComponentActivity.edgeToEdgeWithStyle() {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(
            scrim = Color.Black.copy(alpha = 0.15f).toArgb(),
            darkScrim = Color.Black.copy(alpha = 0.15f).toArgb(),
        ),
    )
}
