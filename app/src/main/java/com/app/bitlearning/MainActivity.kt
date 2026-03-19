/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.app.bitlearning.core.common.theme.BitLearningTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Holds the deep link URI from either cold-start or hot-start intents
    private val deepLinkUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Capture deep link URI when app is launched from a cold start via App Link
        deepLinkUri.value = intent?.data

        setContent {
            BitLearningTheme {
                val navController = rememberNavController()
                
                // Python compiler launcher
                val launchPythonCompiler: () -> Unit = {
                    try {
                        val intent = Intent(
                            this@MainActivity,
                            Class.forName("github.psicodes.ktxpy.activities.HomeActivity")
                        )
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                BitLearningNavGraph(
                    navController = navController,
                    deepLinkUri = deepLinkUri.value,
                    onLaunchPythonCompiler = launchPythonCompiler,
                )
            }
        }
    }

    /**
     * Called when the app is already running (singleTask launch mode) and a new
     * App Link intent arrives. Updates the deep link state so the NavGraph reacts.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkUri.value = intent.data
    }
}
