/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.models

data class Camera(
    val id: Int,
    val name: String = "Camera",
    val type: CameraType = CameraType.MIRRORLESS,
    val price: Double = 999.99,
    val currencyWithSymbol: CurrencyWithSymbol = CurrencyWithSymbol.USD,
    val icon: String = "https://example.com/camera_icon.png", // Placeholder for camera icon URL
    val description: String = "Capture photos and videos using your device's camera.",
    val isEnabled: Boolean = true,
    val isVisible: Boolean = true,
)

enum class CameraType {
    DIGITAL,
    FILM,
    DSLR,
    MIRRORLESS,
    ACTION,
    FULL_FRAME,
    COMPACT,
    INSTANT,
    POINT_AND_SHOOT,
}

enum class CurrencyWithSymbol(val symbol: String) {
    USD("$"),
    VND("₫"),
    EUR("€"),
    GBP("£"),
    JPY("¥"),
    CNY("¥"),
    INR("₹"),
    RUB("₽"),
    AUD("A$"),
    CAD("C$"),
    CHF("CHF"),
    ;

    override fun toString(): String = symbol
}
