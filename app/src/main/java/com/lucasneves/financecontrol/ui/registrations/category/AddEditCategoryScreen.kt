package com.lucasneves.financecontrol.ui.registrations.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasneves.financecontrol.data.model.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(
    categoryId: String?,
    onNavigateBack: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(categoryId) {
        if (categoryId != null) viewModel.initEdit(categoryId) else viewModel.loadCategories()
    }
    LaunchedEffect(state.isSuccess) { if (state.isSuccess) onNavigateBack() }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    // Determine if current category has children (subcategories can't have children)
    val hasChildren = state.categories.any { it.parentId == categoryId }
    val rootsOfSameType = state.categories.filter {
        it.type == state.type && it.parentId == null && it.id != categoryId
    }
    // Only show parent selector if: no children AND (not edit mode OR it's already a subcategory)
    val canSelectParent = !hasChildren && rootsOfSameType.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Editar Categoria" else "Nova Categoria") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Column(Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.setName(it) },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf(CategoryType.EXPENSE to "Despesa", CategoryType.INCOME to "Receita")
                        .forEachIndexed { index, (type, label) ->
                            SegmentedButton(
                                selected = state.type == type,
                                onClick = { viewModel.setType(type) },
                                shape = SegmentedButtonDefaults.itemShape(index, 2)
                            ) { Text(label) }
                        }
                }

                if (canSelectParent) {
                    ParentCategoryDropdown(
                        roots = rootsOfSameType,
                        selectedParentId = state.parentId,
                        onSelect = { viewModel.setParentId(it) }
                    )
                }

                Button(onClick = { viewModel.save(categoryId) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Salvar")
                }

                if (state.isEditMode) {
                    OutlinedButton(
                        onClick = { viewModel.requestDelete(categoryId ?: "") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Excluir") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParentCategoryDropdown(
    roots: List<com.lucasneves.financecontrol.data.model.Category>,
    selectedParentId: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = roots.find { it.id == selectedParentId }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected?.name ?: "Nenhuma (categoria raiz)",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoria Pai (opcional)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Nenhuma (categoria raiz)") },
                onClick = { onSelect(null); expanded = false }
            )
            roots.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.name) },
                    onClick = { onSelect(cat.id); expanded = false }
                )
            }
        }
    }
}
