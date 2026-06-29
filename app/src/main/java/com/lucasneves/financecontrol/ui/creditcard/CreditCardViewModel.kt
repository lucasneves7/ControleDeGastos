package com.lucasneves.financecontrol.ui.creditcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasneves.financecontrol.data.model.AccountType
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
import kotlinx.datetime.plus
import javax.inject.Inject

@HiltViewModel
class CreditCardViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreditCardUiState())
    val uiState: StateFlow<CreditCardUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !refresh, isRefreshing = refresh, error = null) }
            try {
                val accounts = accountRepository.getAccounts().filter { it.type == AccountType.CREDIT }
                val categories = categoryRepository.getCategories()
                val transactions = transactionRepository.getTransactions()
                val selectedId = _uiState.value.selectedAccountId.ifEmpty {
                    accounts.firstOrNull()?.id ?: ""
                }
                val (start, end) = computeBillingPeriod(accounts.find { it.id == selectedId })
                _uiState.update {
                    it.copy(
                        isLoading = false, isRefreshing = false,
                        creditAccounts = accounts,
                        allTransactions = transactions,
                        categories = categories,
                        selectedAccountId = selectedId,
                        billingPeriodStart = start,
                        billingPeriodEnd = end
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) }
            }
        }
    }

    fun selectAccount(id: String) {
        val account = _uiState.value.creditAccounts.find { it.id == id }
        val (start, end) = computeBillingPeriod(account)
        _uiState.update { it.copy(selectedAccountId = id, billingPeriodStart = start, billingPeriodEnd = end) }
    }

    fun previousBillingPeriod() {
        val start = _uiState.value.billingPeriodStart ?: return
        val newEnd = start.minus(1, DateTimeUnit.DAY)
        val account = _uiState.value.creditAccounts.find { it.id == _uiState.value.selectedAccountId }
        val billingDay = account?.billingDay ?: 1
        val newStart = LocalDate(newEnd.year, newEnd.month, 1)
            .let { firstOfMonth ->
                if (billingDay >= newEnd.dayOfMonth) firstOfMonth.minus(1, DateTimeUnit.MONTH)
                else firstOfMonth
            }
            .let { LocalDate(it.year, it.month, minOf(billingDay + 1, daysInMonth(it.year, it.monthNumber))) }
        _uiState.update { it.copy(billingPeriodStart = newStart, billingPeriodEnd = newEnd) }
    }

    fun nextBillingPeriod() {
        val end = _uiState.value.billingPeriodEnd ?: return
        val newStart = end.plus(1, DateTimeUnit.DAY)
        val account = _uiState.value.creditAccounts.find { it.id == _uiState.value.selectedAccountId }
        val billingDay = account?.billingDay ?: 1
        val nextMonth = newStart.plus(1, DateTimeUnit.MONTH)
        val newEnd = LocalDate(
            if (billingDay >= newStart.dayOfMonth) newStart.year else nextMonth.year,
            if (billingDay >= newStart.dayOfMonth) newStart.month else nextMonth.month,
            minOf(billingDay, daysInMonth(
                if (billingDay >= newStart.dayOfMonth) newStart.year else nextMonth.year,
                if (billingDay >= newStart.dayOfMonth) newStart.monthNumber else nextMonth.monthNumber
            ))
        )
        _uiState.update { it.copy(billingPeriodStart = newStart, billingPeriodEnd = newEnd) }
    }

    fun getBillingTransactions() = _uiState.value.run {
        val start = billingPeriodStart ?: return@run emptyList()
        val end = billingPeriodEnd ?: return@run emptyList()
        allTransactions.filter { t ->
            t.accountId == selectedAccountId &&
                    t.date >= start.toString() && t.date <= end.toString()
        }.sortedByDescending { it.date }
    }

    fun getBillingTotal() = getBillingTransactions().sumOf { it.amount }

    private fun computeBillingPeriod(account: com.lucasneves.financecontrol.data.model.Account?): Pair<LocalDate?, LocalDate?> {
        val billingDay = account?.billingDay ?: return null to null
        val today = DateUtils.currentMonth()
        val endYear = today.year
        val endMonth = today.monthNumber
        val end = LocalDate(endYear, endMonth, minOf(billingDay, daysInMonth(endYear, endMonth)))
        val prevMonth = today.minus(1, DateTimeUnit.MONTH)
        val start = LocalDate(
            prevMonth.year, prevMonth.monthNumber,
            minOf(billingDay + 1, daysInMonth(prevMonth.year, prevMonth.monthNumber))
        )
        return start to end
    }

    private fun daysInMonth(year: Int, month: Int): Int {
        return java.time.YearMonth.of(year, month).lengthOfMonth()
    }
}
