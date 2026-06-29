package com.lucasneves.financecontrol.ui.registrations.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesListScreen(
    onNavigateBack: () -> Unit,
    onAddCategory: () -> Unit,
    onEditCategory: (String) -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadCategories() }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    if (state.showReassignDialog) {
        ReassignCategoryDialog(
            categories = state.categories.filter {
                it.id != state.deletingCategoryId &&
                        it.type == state.categories.find { c -> c.id == state.deletingCategoryId }?.type
            },
            onConfirm = { viewModel.confirmDelete(it) },
            onDismiss = { viewModel.cancelDelete() }
        )
    }

    val tabs = listOf(CategoryType.EXPENSE, CategoryType.INCOME)
    val tabIndex = tabs.indexOf(state.selectedTab)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorias") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCategory) {
                Icon(Icons.Default.Add, contentDescription = "Nova categoria")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PrimaryTabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, type ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { viewModel.setTab(type) },
                        text = { Text(if (type == CategoryType.EXPENSE) "Despesas" else "Receitas") }
                    )
                }
            }

            if (state.isLoading) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator() }
            } else {
                val roots = state.categories.filter { it.type == state.selectedTab && it.parentId == null }
                val children = state.categories.filter { it.type == state.selectedTab && it.parentId != null }

                LazyColumn(Modifier.fillMaxSize()) {
                    roots.forEach { root ->
                        item {
                            CategoryRow(
                                category = root,
                                isRoot = true,
                                onEdit = { onEditCategory(root.id) },
                                onDelete = { viewModel.requestDelete(root.id) }
                            )
                            HorizontalDivider()
                        }
                        val subCategories = children.filter { it.parentId == root.id }
                        items(subCategories, key = { it.id }) { child ->
                            CategoryRow(
                                category = child,
                                isRoot = false,
                                onEdit = { onEditCategory(child.id) },
                                onDelete = { viewModel.requestDelete(child.id) }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryRow(category: Category, isRoot: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); false } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(Modifier.fillMaxSize().padding(end = 16.dp), contentAlignment = Alignment.CenterEnd) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = if (isRoot) category.name else "  ${category.name}",
                    fontWeight = if (isRoot) FontWeight.SemiBold else FontWeight.Normal
                )
            },
            modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReassignCategoryDialog(
    categories: List<Category>,
    onConfirm: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedId by remember { mutableStateOf(categories.firstOrNull()?.id) }
    var expanded by remember { mutableStateOf(false) }
    val selected = categories.find { it.id == selectedId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir categoria") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Esta categoria possui lançamentos. Selecione uma categoria para reatribuí-los:")
                if (categories.isNotEmpty()) {
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = selected?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = { selectedId = cat.id; expanded = false }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(selectedId) }) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
