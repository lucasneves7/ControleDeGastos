package com.lucasneves.financecontrol.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasneves.financecontrol.data.model.AccountType
import com.lucasneves.financecontrol.data.model.DayMarkType
import com.lucasneves.financecontrol.data.model.Transaction
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
class OverviewViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !refresh, isRefreshing = refresh, error = null) }
            try {
                val accounts = accountRepository.getAccounts()
                val categories = categoryRepository.getCategories()
                val transactions = transactionRepository.getTransactions()
                val month = _uiState.value.selectedMonth
                _uiState.update {
                    it.copy(
                        isLoading = false, isRefreshing = false,
                        accounts = accounts,
                        categories = categories,
                        allTransactions = transactions,
                        totalBalance = accounts
                            .filter { a -> a.type != AccountType.CREDIT }
                            .sumOf { a -> a.balance },
                        monthlyIncome = transactions
                            .filter { t -> t.type == TransactionType.INCOME && DateUtils.isInMonth(t.date, month) }
                            .sumOf { t -> t.amount },
                        monthlyExpense = transactions
                            .filter { t -> t.type == TransactionType.EXPENSE && DateUtils.isInMonth(t.date, month) }
                            .sumOf { t -> t.amount },
                        dayMarks = computeDayMarks(transactions)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) }
            }
        }
    }

    fun selectMonth(month: LocalDate) {
        val transactions = _uiState.value.allTransactions
        _uiState.update {
            it.copy(
                selectedMonth = month,
                monthlyIncome = transactions
                    .filter { t -> t.type == TransactionType.INCOME && DateUtils.isInMonth(t.date, month) }
                    .sumOf { t -> t.amount },
                monthlyExpense = transactions
                    .filter { t -> t.type == TransactionType.EXPENSE && DateUtils.isInMonth(t.date, month) }
                    .sumOf { t -> t.amount }
            )
        }
    }

    fun selectDay(day: LocalDate) = _uiState.update { it.copy(selectedDay = day) }
    fun clearSelectedDay() = _uiState.update { it.copy(selectedDay = null) }

    fun getMonthTransactions() = _uiState.value.run {
        allTransactions.filter { DateUtils.isInMonth(it.date, selectedMonth) }
    }

    fun getDayTransactions(day: LocalDate) = _uiState.value.allTransactions
        .filter { it.date == day.toString() }
        .sortedBy { it.date }

    fun getExpensesByCategory() = _uiState.value.run {
        val month = selectedMonth
        val expenses = allTransactions.filter {
            it.type == TransactionType.EXPENSE && DateUtils.isInMonth(it.date, month)
        }
        categories
            .filter { it.parentId == null && it.type.name == "EXPENSE" }
            .map { parent ->
                val childIds = categories.filter { c -> c.parentId == parent.id }.map { c -> c.id }
                val total = expenses.filter { e -> e.categoryId in childIds + parent.id }.sumOf { e -> e.amount }
                parent to total
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(5)
    }

    private fun computeDayMarks(transactions: List<Transaction>): Map<LocalDate, DayMarkType> =
        transactions.groupBy { it.date }.mapKeys { (date, _) ->
            runCatching { LocalDate.parse(date) }.getOrNull()
        }.filterKeys { it != null }.mapKeys { it.key!! }.mapValues { (_, txs) ->
            val hasIncome = txs.any { it.type == TransactionType.INCOME }
            val hasExpense = txs.any { it.type == TransactionType.EXPENSE }
            when {
                hasIncome && hasExpense -> DayMarkType.BOTH
                hasIncome -> DayMarkType.INCOME_ONLY
                hasExpense -> DayMarkType.EXPENSE_ONLY
                else -> DayMarkType.NONE
            }
        }
}
