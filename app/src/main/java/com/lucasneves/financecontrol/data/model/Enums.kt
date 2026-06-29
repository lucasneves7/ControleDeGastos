package com.lucasneves.financecontrol.data.model

enum class TransactionType { INCOME, EXPENSE, TRANSFER }

enum class AccountType { CASH, CHECKING, SAVINGS, CREDIT }

enum class CategoryType { INCOME, EXPENSE }

enum class DayMarkType { NONE, INCOME_ONLY, EXPENSE_ONLY, BOTH }

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
