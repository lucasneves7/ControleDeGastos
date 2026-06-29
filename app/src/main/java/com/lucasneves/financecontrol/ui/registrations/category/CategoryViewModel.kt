package com.lucasneves.financecontrol.ui.registrations.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.CategoryType
import com.lucasneves.financecontrol.data.repository.CategoryRepository
import com.lucasneves.financecontrol.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val categories = categoryRepository.getCategories()
                _uiState.update { it.copy(isLoading = false, categories = categories) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun initEdit(categoryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val categories = categoryRepository.getCategories()
                val category = categories.find { it.id == categoryId }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = categories,
                        isEditMode = true,
                        name = category?.name ?: "",
                        type = category?.type ?: CategoryType.EXPENSE,
                        parentId = category?.parentId
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setName(v: String) = _uiState.update { it.copy(name = v) }
    fun setType(v: CategoryType) = _uiState.update { it.copy(type = v, parentId = null) }
    fun setParentId(v: String?) = _uiState.update { it.copy(parentId = v) }
    fun setTab(v: CategoryType) = _uiState.update { it.copy(selectedTab = v) }

    fun save(editingCategoryId: String? = null) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Informe o nome da categoria.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                if (editingCategoryId != null) {
                    val existing = categoryRepository.getCategories().find { it.id == editingCategoryId }
                        ?: return@launch
                    categoryRepository.updateCategory(
                        existing.copy(name = state.name, type = state.type, parentId = state.parentId)
                    )
                } else {
                    categoryRepository.addCategory(
                        name = state.name,
                        type = state.type,
                        parentId = state.parentId
                    )
                }
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun requestDelete(categoryId: String) {
        _uiState.update { it.copy(showReassignDialog = true, deletingCategoryId = categoryId) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(showReassignDialog = false, deletingCategoryId = null) }
    }

    fun confirmDelete(reassignToCategoryId: String?) {
        val deletingId = _uiState.value.deletingCategoryId ?: return
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showReassignDialog = false) }
            try {
                // Also delete child categories if deleting a root
                val children = state.categories.filter { it.parentId == deletingId }
                if (reassignToCategoryId != null) {
                    transactionRepository.reassignCategory(deletingId, reassignToCategoryId)
                    children.forEach { child ->
                        transactionRepository.reassignCategory(child.id, reassignToCategoryId)
                    }
                }
                children.forEach { child -> categoryRepository.deleteCategory(child.id) }
                categoryRepository.deleteCategory(deletingId)
                val categories = categoryRepository.getCategories()
                _uiState.update { it.copy(isLoading = false, categories = categories, deletingCategoryId = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message, deletingCategoryId = null) }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
