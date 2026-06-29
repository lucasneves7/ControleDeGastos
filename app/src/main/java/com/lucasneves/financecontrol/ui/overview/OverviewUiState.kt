package com.lucasneves.financecontrol.ui.overview

import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.DayMarkType
import com.lucasneves.financecontrol.data.model.Transaction
import com.lucasneves.financecontrol.util.DateUtils
import kotlinx.datetime.LocalDate

data class OverviewUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedMonth: LocalDate = DateUtils.currentMonth(),
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val allTransactions: List<Transaction> = emptyList(),
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val dayMarks: Map<LocalDate, DayMarkType> = emptyMap(),
    val selectedDay: LocalDate? = null
)
