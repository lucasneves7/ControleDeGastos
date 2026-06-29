package com.lucasneves.financecontrol.ui.reports

import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.Transaction
import com.lucasneves.financecontrol.util.DateUtils
import kotlinx.datetime.LocalDate

data class ReportsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedMonth: LocalDate = DateUtils.currentMonth(),
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val selectedAccountId: String = "",
    val selectedTab: ReportTab = ReportTab.CASH_FLOW
)

enum class ReportTab(val label: String) {
    CASH_FLOW("Fluxo de Caixa"),
    BY_CATEGORY("Por Categoria"),
    STATEMENT("Extrato")
}