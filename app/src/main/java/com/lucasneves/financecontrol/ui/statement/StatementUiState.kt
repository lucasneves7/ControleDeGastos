package com.lucasneves.financecontrol.ui.statement

import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.Transaction
import com.lucasneves.financecontrol.util.DateUtils
import kotlinx.datetime.LocalDate

enum class StatementFilter(val label: String) {
    ALL("Todos"),
    INCOME("Receitas"),
    EXPENSE("Despesas"),
    TRANSFER("Transferências")
}

data class StatementUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val accounts: List<Account> = emptyList(),
    val allTransactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedAccountId: String = "",
    val selectedMonth: LocalDate = DateUtils.currentMonth(),
    val filter: StatementFilter = StatementFilter.ALL
)
