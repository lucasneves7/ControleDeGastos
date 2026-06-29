package com.lucasneves.financecontrol.ui.statement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasneves.financecontrol.data.model.AccountType
import com.lucasneves.financecontrol.data.model.TransactionType
import com.lucasneves.financecontrol.data.repository.AccountRepository
import com.lucasneves.financecontrol.data.repository.CategoryRepository
import com.lucasneves.financecontrol.data.repository.TransactionRepository
import com.lucasneves.financecontrol.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatementViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatementUiState())
    val uiState: StateFlow<StatementUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !refresh, isRefreshing = refresh, error = null) }
            try {
                val accounts = accountRepository.getAccounts().filter { it.type != AccountType.CREDIT }
                val categories = categoryRepository.getCategories()
                val transactions = transactionRepository.getTransactions()
                val selectedId = _uiState.value.selectedAccountId.ifEmpty {
                    accounts.firstOrNull()?.id ?: ""
                }
                _uiState.update {
                    it.copy(
                        isLoading = false, isRefreshing = false,
                        accounts = accounts, allTransactions = transactions,
                        categories = categories, selectedAccountId = selectedId
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) }
            }
        }
    }

    fun selectAccount(id: String) = _uiState.update { it.copy(selectedAccountId = id) }
    fun selectMonth(month: LocalDate) = _uiState.update { it.copy(selectedMonth = month) }
    fun setFilter(f: StatementFilter) = _uiState.update { it.copy(filter = f) }

    fun getFilteredTransactions() = _uiState.value.run {
        allTransactions
            .filter { it.accountId == selectedAccountId && DateUtils.isInMonth(it.date, selectedMonth) }
            .filter { t ->
                when (filter) {
                    StatementFilter.ALL -> true
                    StatementFilter.INCOME -> t.type == TransactionType.INCOME
                    StatementFilter.EXPENSE -> t.type == TransactionType.EXPENSE
                    StatementFilter.TRANSFER -> t.type == TransactionType.TRANSFER
                }
            }
            .sortedBy { it.date }
    }

    fun getMonthlyIncome() = _uiState.value.run {
        allTransactions.filter {
            it.accountId == selectedAccountId &&
                    DateUtils.isInMonth(it.date, selectedMonth) &&
                    it.type == TransactionType.INCOME
        }.sumOf { it.amount }
    }

    fun getMonthlyExpense() = _uiState.value.run {
        allTransactions.filter {
            it.accountId == selectedAccountId &&
                    DateUtils.isInMonth(it.date, selectedMonth) &&
                    it.type == TransactionType.EXPENSE
        }.sumOf { it.amount }
    }
}
