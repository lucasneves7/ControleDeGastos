package com.lucasneves.financecontrol.ui.registrations.account

import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.AccountType

data class AccountUiState(
    val isLoading: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false,
    // AddEdit fields
    val isEditMode: Boolean = false,
    val name: String = "",
    val type: AccountType = AccountType.CASH,
    val initialBalance: String = "0",
    val creditLimit: String = "",
    val billingDay: String = "",
    val dueDay: String = "",
    // Delete with reassign
    val showReassignDialog: Boolean = false,
    val deletingAccountId: String? = null
)
