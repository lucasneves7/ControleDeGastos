package com.lucasneves.financecontrol.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasneves.financecontrol.data.model.Category
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val accounts = accountRepository.getAccounts()
                val categories = categoryRepository.getCategories()
                val transactions = transactionRepository.getTransactions()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        accounts = accounts,
                        categories = categories,
                        transactions = transactions,
                        selectedAccountId = accounts.firstOrNull()?.id ?: ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectMonth(firstOfMonth: LocalDate) = _uiState.update { it.copy(selectedMonth = firstOfMonth) }
    fun selectTab(tab: ReportTab) = _uiState.update { it.copy(selectedTab = tab) }
    fun selectAccount(id: String) = _uiState.update { it.copy(selectedAccountId = id) }

    fun getCashFlowData(): List<Pair<LocalDate, Pair<Double, Double>>> {
        val months = (5 downTo 0).map { offset ->
            _uiState.value.selectedMonth.minus(offset, DateTimeUnit.MONTH)
        }
        return months.map { month ->
            val txs = _uiState.value.transactions.filter { DateUtils.isInMonth(it.date, month) }
            val income = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            month to (income to expense)
        }
    }

    fun getExpensesByCategory(): List<Pair<Category, Double>> {
        val month = _uiState.value.selectedMonth
        val state = _uiState.value
        val expenses = state.transactions.filter {
            it.type == TransactionType.EXPENSE && DateUtils.isInMonth(it.date, month)
        }
        return state.categories
            .filter { it.parentId == null && it.type.name == "EXPENSE" }
            .map { parent ->
                val childIds = state.categories.filter { it.parentId == parent.id }.map { it.id }
                val allIds = childIds + parent.id
                val total = expenses.filter { it.categoryId in allIds }.sumOf { it.amount }
                parent to total
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
    }
}