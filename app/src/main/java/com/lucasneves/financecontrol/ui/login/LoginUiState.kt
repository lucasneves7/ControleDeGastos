package com.lucasneves.financecontrol.ui.login

import android.app.PendingIntent

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class NeedScopeConsent(val pendingIntent: PendingIntent) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
