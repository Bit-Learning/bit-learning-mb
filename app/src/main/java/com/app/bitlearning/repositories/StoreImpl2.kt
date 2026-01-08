/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.repositories

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StoreImpl2 @Inject constructor(@ApplicationContext context: Context) : Store {
    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    override fun getValue(key: String): String = sharedPreferences.getString(key, "") ?: ""

    override fun setValue(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }
}
