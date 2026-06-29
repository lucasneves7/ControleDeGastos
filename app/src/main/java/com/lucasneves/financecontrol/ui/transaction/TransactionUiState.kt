package com.lucasneves.financecontrol.ui.transaction

import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.TransactionType

data class TransactionUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val date: String = "",
    val description: String = "",
    val selectedAccountId: String = "",
    val selectedCategoryId: String = "",
    val selectedToAccountId: String = "",
    val isEditMode: Boolean = false
)
