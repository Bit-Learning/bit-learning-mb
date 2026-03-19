/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.File

@HiltAndroidApp
class BitLearningApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Python runtime components
        initializePythonRuntime()
    }
    
    private fun initializePythonRuntime() {
        try {
            // Initialize crash handler for Python module
            github.psicodes.ktxpy.utils.CrashHandler.INSTANCE.init(this)
            
            // Initialize Timber for logging
            if (timber.log.Timber.forest().isEmpty()) {
                Timber.plant(Timber.DebugTree())
            }
            
            // Create Python files directory
            val pythonFilesDir = File(filesDir.absolutePath + "/pythonFiles")
            if (!pythonFilesDir.exists()) {
                pythonFilesDir.mkdir()
            }
            github.psicodes.ktxpy.utils.PythonFileManager.filesDir = pythonFilesDir.absolutePath
            github.psicodes.ktxpy.utils.PythonFileManager.init()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
