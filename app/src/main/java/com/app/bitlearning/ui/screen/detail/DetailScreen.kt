/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.ui.screen.detail

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun DetailScreen(navController: NavHostController, hiltViewModel: DetailViewModel) {
    TextButton(onClick = { navController.popBackStack() }) {
        Text(text = "Back")
    }
}
