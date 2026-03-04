/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.payment.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class PaymentMethodType { CARD, EWALLET }

data class PaymentMethod(
    val id: String,
    val type: PaymentMethodType,
    val name: String,
    val detail: String, // e.g. "**** 1234" or phone number
    val logoUrl: String? = null,
    val isDefault: Boolean = false,
    val isConnected: Boolean = true,
)

data class PaymentSettingState(
    val methods: List<PaymentMethod> = defaultPaymentMethods(),
    val selectedId: String = "pm_1",
    val isSaving: Boolean = false,
)

@HiltViewModel
class PaymentSettingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PaymentSettingState())
    val state: StateFlow<PaymentSettingState> = _state

    fun selectMethod(id: String) = _state.update { it.copy(selectedId = id) }

    fun connectWallet(id: String) {
        _state.update { s ->
            s.copy(methods = s.methods.map { if (it.id == id) it.copy(isConnected = true) else it })
        }
    }

    fun saveChanges(onDone: () -> Unit) {
        _state.update { it.copy(isSaving = true) }
        // Simulate save — replace with real API call when ready
        _state.update { s ->
            s.copy(
                isSaving = false,
                methods = s.methods.map { it.copy(isDefault = it.id == s.selectedId) },
            )
        }
        onDone()
    }
}

private fun defaultPaymentMethods() = listOf(
    PaymentMethod(
        id = "pm_1",
        type = PaymentMethodType.CARD,
        name = "Visa **** 1234",
        detail = "Hết hạn 12/26",
        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Visa_Inc._logo.svg/800px-Visa_Inc._logo.svg.png",
        isDefault = true,
        isConnected = true,
    ),
    PaymentMethod(
        id = "pm_2",
        type = PaymentMethodType.CARD,
        name = "Mastercard **** 5678",
        detail = "Hết hạn 09/25",
        logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Mastercard_2019_logo.svg/800px-Mastercard_2019_logo.svg.png",
        isDefault = false,
        isConnected = true,
    ),
    PaymentMethod(
        id = "pm_momo",
        type = PaymentMethodType.EWALLET,
        name = "Ví MoMo",
        detail = "09x xxx 8888",
        logoUrl = "https://upload.wikimedia.org/wikipedia/vi/f/fe/MoMo_Logo.png",
        isDefault = false,
        isConnected = true,
    ),
    PaymentMethod(
        id = "pm_zalopay",
        type = PaymentMethodType.EWALLET,
        name = "ZaloPay",
        detail = "Chưa kết nối",
        logoUrl = "https://cdn.haitrieu.com/wp-content/uploads/2022/10/Logo-ZaloPay-Square.png",
        isDefault = false,
        isConnected = false,
    ),
)
