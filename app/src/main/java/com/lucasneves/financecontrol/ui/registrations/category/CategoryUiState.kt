package com.lucasneves.financecontrol.ui.registrations.category

import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.CategoryType

data class CategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val isSuccess: Boolean = false,
    // AddEdit fields
    val isEditMode: Boolean = false,
    val name: String = "",
    val type: CategoryType = CategoryType.EXPENSE,
    val parentId: String? = null,
    val selectedTab: CategoryType = CategoryType.EXPENSE,
    // Delete with reassign
    val showReassignDialog: Boolean = false,
    val deletingCategoryId: String? = null
)
