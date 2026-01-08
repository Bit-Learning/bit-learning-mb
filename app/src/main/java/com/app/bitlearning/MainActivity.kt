/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.app.bitlearning.extensions.edgeToEdgeWithStyle
import com.app.bitlearning.ui.theme.AppTheme
import com.app.bitlearning.utils.CurrencyManager
import com.app.bitlearning.utils.LanguageManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var currencyManager: CurrencyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        edgeToEdgeWithStyle()

        // Initialize currency manager with language manager
        LanguageManager.setCurrencyManager(currencyManager)

        val currentLang = LanguageManager.getLocale(this)
        LanguageManager.setLocale(this, currentLang)

        // Initialize exchange rate on app start
        lifecycleScope.launch {
            currencyManager.updateExchangeRate()
        }

        setContent { App() }
    }
}

@Composable
fun App() {
    AppTheme {
        MainScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
@RestrictTo(RestrictTo.Scope.TESTS)
private fun AppPreview() {
    AppTheme {
        MainScreen()
    }
}
