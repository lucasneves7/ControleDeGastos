package com.lucasneves.financecontrol.data.model

data class Transaction(
    val id: String,
    val date: String,
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val accountId: String,
    val categoryId: String?,
    val toAccountId: String?
)
