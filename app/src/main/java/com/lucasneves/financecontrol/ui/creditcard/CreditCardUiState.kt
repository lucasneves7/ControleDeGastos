package com.lucasneves.financecontrol.ui.creditcard

import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.Transaction
import kotlinx.datetime.LocalDate

data class CreditCardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val creditAccounts: List<Account> = emptyList(),
    val allTransactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedAccountId: String = "",
    val billingPeriodStart: LocalDate? = null,
    val billingPeriodEnd: LocalDate? = null
)
