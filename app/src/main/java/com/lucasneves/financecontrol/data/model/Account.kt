package com.lucasneves.financecontrol.data.model

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val creditLimit: Double? = null,
    val billingDay: Int? = null,
    val dueDay: Int? = null,
    val createdAt: String
)
