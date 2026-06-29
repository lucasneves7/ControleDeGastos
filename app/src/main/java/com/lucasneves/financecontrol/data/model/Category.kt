package com.lucasneves.financecontrol.data.model

data class Category(
    val id: String,
    val name: String,
    val parentId: String?,
    val type: CategoryType,
    val isDefault: Boolean
)
