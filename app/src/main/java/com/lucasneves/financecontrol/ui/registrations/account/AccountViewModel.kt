package com.lucasneves.financecontrol.ui.registrations.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.AccountType
import com.lucasneves.financecontrol.data.repository.AccountRepository
import com.lucasneves.financecontrol.data.repository.TransactionRepository
import com.lucasneves.financecontrol.util.CurrencyUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val accounts = accountRepository.getAccounts()
                _uiState.update { it.copy(isLoading = false, accounts = accounts) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun initEdit(accountId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val accounts = accountRepository.getAccounts()
                val account = accounts.find { it.id == accountId }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        accounts = accounts,
                        isEditMode = true,
                        name = account?.name ?: "",
                        type = account?.type ?: AccountType.CASH,
                        initialBalance = account?.balance?.toString() ?: "0",
                        creditLimit = account?.creditLimit?.toString() ?: "",
                        billingDay = account?.billingDay?.toString() ?: "",
                        dueDay = account?.dueDay?.toString() ?: ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setName(v: String) = _uiState.update { it.copy(name = v) }
    fun setType(v: AccountType) = _uiState.update { it.copy(type = v) }
    fun setInitialBalance(v: String) = _uiState.update { it.copy(initialBalance = v) }
    fun setCreditLimit(v: String) = _uiState.update { it.copy(creditLimit = v) }
    fun setBillingDay(v: String) = _uiState.update { it.copy(billingDay = v) }
    fun setDueDay(v: String) = _uiState.update { it.copy(dueDay = v) }

    fun save(editingAccountId: String? = null) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Informe o nome da conta.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                if (editingAccountId != null) {
                    val existing = accountRepository.getAccounts().find { it.id == editingAccountId }
                        ?: return@launch
                    accountRepository.updateAccount(
                        existing.copy(
                            name = state.name,
                            type = state.type,
                            creditLimit = state.creditLimit.toDoubleOrNull(),
                            billingDay = state.billingDay.toIntOrNull(),
                            dueDay = state.dueDay.toIntOrNull()
                        )
                    )
                } else {
                    accountRepository.addAccount(
                        name = state.name,
                        type = state.type,
                        initialBalance = CurrencyUtils.parse(state.initialBalance),
                        creditLimit = state.creditLimit.toDoubleOrNull(),
                        billingDay = state.billingDay.toIntOrNull(),
                        dueDay = state.dueDay.toIntOrNull()
                    )
                }
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun requestDelete(accountId: String) {
        _uiState.update { it.copy(showReassignDialog = true, deletingAccountId = accountId) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(showReassignDialog = false, deletingAccountId = null) }
    }

    fun confirmDelete(reassignToAccountId: String?) {
        val deletingId = _uiState.value.deletingAccountId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showReassignDialog = false) }
            try {
                if (reassignToAccountId != null) {
                    transactionRepository.reassignAccount(deletingId, reassignToAccountId)
                }
                accountRepository.deleteAccount(deletingId)
                val accounts = accountRepository.getAccounts()
                _uiState.update { it.copy(isLoading = false, accounts = accounts, deletingAccountId = null, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message, deletingAccountId = null) }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
