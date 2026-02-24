/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.log

interface MainLog {
    fun d(tag: String, msg: String)
    fun i(tag: String, msg: String)
    fun e(tag: String, msg: String)
}
