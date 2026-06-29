package com.lucasneves.financecontrol.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.lucasneves.financecontrol.data.repository.AuthRepository
import com.lucasneves.financecontrol.data.repository.CategoryRepository
import com.lucasneves.financecontrol.data.repository.SpreadsheetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val spreadsheetRepository: SpreadsheetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun isAlreadySignedIn(): Boolean = authRepository.isSignedIn()

    fun signIn(context: Context) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                authRepository.signIn(context)
                val pendingIntent = authRepository.requestGoogleAuthorization()
                if (pendingIntent != null) {
                    _uiState.value = LoginUiState.NeedScopeConsent(pendingIntent)
                } else {
                    completeLogin()
                }
            } catch (e: GetCredentialCancellationException) {
                if (e.message?.contains("reauth", ignoreCase = true) == true) {
                    _uiState.value = LoginUiState.Error(
                        "Sua conta Google precisa ser reautenticada. " +
                        "Acesse Configurações → Contas → Google e verifique sua conta."
                    )
                } else {
                    _uiState.value = LoginUiState.Idle
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Erro ao fazer login.")
            }
        }
    }

    fun onAuthorizationGranted() {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                completeLogin()
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Erro ao configurar planilha.")
            }
        }
    }

    private suspend fun completeLogin() {
        spreadsheetRepository.getOrCreateSpreadsheet()
        val categories = categoryRepository.getCategories()
        if (categories.isEmpty()) {
            categoryRepository.addDefaultCategories()
        }
        _uiState.value = LoginUiState.Success
    }

    fun clearError() {
        _uiState.value = LoginUiState.Idle
    }
}
