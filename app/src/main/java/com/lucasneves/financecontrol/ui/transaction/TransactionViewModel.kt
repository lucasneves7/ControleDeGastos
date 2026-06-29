package com.lucasneves.financecontrol.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasneves.financecontrol.data.model.TransactionType
import com.lucasneves.financecontrol.data.repository.AccountRepository
import com.lucasneves.financecontrol.data.repository.CategoryRepository
import com.lucasneves.financecontrol.data.repository.TransactionRepository
import com.lucasneves.financecontrol.util.CurrencyUtils
import com.lucasneves.financecontrol.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private var editingTransactionId: String? = null

    fun init(transactionId: String?, prefilledDate: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val accounts = accountRepository.getAccounts()
                val categories = categoryRepository.getCategories()
                if (transactionId != null) {
                    val transaction = transactionRepository.getTransactions().find { it.id == transactionId }
                    editingTransactionId = transactionId
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isEditMode = true,
                            accounts = accounts,
                            categories = categories,
                            type = transaction?.type ?: TransactionType.EXPENSE,
                            amount = transaction?.amount?.toString() ?: "",
                            date = transaction?.date ?: DateUtils.today(),
                            description = transaction?.description ?: "",
                            selectedAccountId = transaction?.accountId ?: accounts.firstOrNull()?.id ?: "",
                            selectedCategoryId = transaction?.categoryId ?: "",
                            selectedToAccountId = transaction?.toAccountId ?: ""
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            accounts = accounts,
                            categories = categories,
                            date = prefilledDate ?: DateUtils.today(),
                            selectedAccountId = accounts.firstOrNull()?.id ?: ""
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setType(type: TransactionType) = _uiState.update { it.copy(type = type) }
    fun setAmount(amount: String) = _uiState.update { it.copy(amount = amount) }
    fun setDate(date: String) = _uiState.update { it.copy(date = date) }
    fun setDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun setAccount(id: String) = _uiState.update { it.copy(selectedAccountId = id) }
    fun setCategory(id: String) = _uiState.update { it.copy(selectedCategoryId = id) }
    fun setToAccount(id: String) = _uiState.update { it.copy(selectedToAccountId = id) }

    fun save() {
        val state = _uiState.value
        val amount = CurrencyUtils.parse(state.amount)
        if (amount <= 0) {
            _uiState.update { it.copy(error = "Informe um valor válido.") }
            return
        }
        if (state.selectedAccountId.isEmpty()) {
            _uiState.update { it.copy(error = "Selecione uma conta.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val transaction = transactionRepository.createNew(
                    date = state.date,
                    description = state.description,
                    amount = amount,
                    type = state.type,
                    accountId = state.selectedAccountId,
                    categoryId = state.selectedCategoryId.ifEmpty { null },
                    toAccountId = if (state.type == TransactionType.TRANSFER) state.selectedToAccountId.ifEmpty { null } else null
                ).let { t ->
                    if (state.isEditMode) t.copy(id = editingTransactionId ?: t.id) else t
                }

                if (state.isEditMode) {
                    val old = transactionRepository.getTransactions().find { it.id == transaction.id }
                    old?.let { revertBalance(it) }
                    transactionRepository.updateTransaction(transaction)
                } else {
                    transactionRepository.addTransaction(transaction)
                }
                applyBalance(transaction)
                _uiState.update { it.copy(isSaving = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun delete() {
        val id = editingTransactionId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val transaction = transactionRepository.getTransactions().find { it.id == id } ?: return@launch
                revertBalance(transaction)
                transactionRepository.deleteTransaction(id)
                _uiState.update { it.copy(isSaving = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    private suspend fun applyBalance(transaction: com.lucasneves.financecontrol.data.model.Transaction) {
        val accounts = accountRepository.getAccounts()
        val account = accounts.find { it.id == transaction.accountId } ?: return
        val newBalance = when (transaction.type) {
            TransactionType.INCOME -> account.balance + transaction.amount
            TransactionType.EXPENSE -> account.balance - transaction.amount
            TransactionType.TRANSFER -> {
                accountRepository.updateBalance(account.id, account.balance - transaction.amount)
                val toAccount = accounts.find { it.id == transaction.toAccountId } ?: return
                accountRepository.updateBalance(toAccount.id, toAccount.balance + transaction.amount)
                return
            }
        }
        accountRepository.updateBalance(account.id, newBalance)
    }

    private suspend fun revertBalance(transaction: com.lucasneves.financecontrol.data.model.Transaction) {
        val accounts = accountRepository.getAccounts()
        val account = accounts.find { it.id == transaction.accountId } ?: return
        val reverted = when (transaction.type) {
            TransactionType.INCOME -> account.balance - transaction.amount
            TransactionType.EXPENSE -> account.balance + transaction.amount
            TransactionType.TRANSFER -> {
                accountRepository.updateBalance(account.id, account.balance + transaction.amount)
                val toAccount = accounts.find { it.id == transaction.toAccountId } ?: return
                accountRepository.updateBalance(toAccount.id, toAccount.balance - transaction.amount)
                return
            }
        }
        accountRepository.updateBalance(account.id, reverted)
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
