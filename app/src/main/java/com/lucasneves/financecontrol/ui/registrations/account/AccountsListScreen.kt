package com.lucasneves.financecontrol.ui.registrations.account

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.AccountType
import com.lucasneves.financecontrol.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsListScreen(
    onNavigateBack: () -> Unit,
    onAddAccount: () -> Unit,
    onEditAccount: (String) -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadAccounts() }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    if (state.showReassignDialog) {
        ReassignAccountDialog(
            accounts = state.accounts.filter { it.id != state.deletingAccountId },
            onConfirm = { viewModel.confirmDelete(it) },
            onDismiss = { viewModel.cancelDelete() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAccount) {
                Icon(Icons.Default.Add, contentDescription = "Nova conta")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Column(
                Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                AccountType.entries.forEach { type ->
                    val accounts = state.accounts.filter { it.type == type }
                    if (accounts.isEmpty()) return@forEach
                    item {
                        Text(
                            text = type.sectionLabel(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        HorizontalDivider()
                    }
                    items(accounts, key = { it.id }) { account ->
                        AccountRow(
                            account = account,
                            onEdit = { onEditAccount(account.id) },
                            onDelete = { viewModel.requestDelete(account.id) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountRow(account: Account, onEdit: () -> Unit, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); false } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize().padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
        }
    ) {
        ListItem(
            headlineContent = { Text(account.name) },
            supportingContent = {
                if (account.type == AccountType.CREDIT) {
                    Text("Limite: ${CurrencyUtils.format(account.creditLimit ?: 0.0)} | Fatura: ${CurrencyUtils.format(account.balance)} | Vence: dia ${account.dueDay ?: "-"}")
                } else {
                    Text(CurrencyUtils.format(account.balance))
                }
            },
            modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReassignAccountDialog(
    accounts: List<Account>,
    onConfirm: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
    var expanded by remember { mutableStateOf(false) }
    val selected = accounts.find { it.id == selectedId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir conta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Esta conta possui lançamentos. Selecione uma conta para reatribuí-los:")
                if (accounts.isNotEmpty()) {
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = selected?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            accounts.forEach { acc ->
                                DropdownMenuItem(
                                    text = { Text(acc.name) },
                                    onClick = { selectedId = acc.id; expanded = false }
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

private fun AccountType.sectionLabel() = when (this) {
    AccountType.CASH -> "CARTEIRA"
    AccountType.CHECKING -> "CONTA CORRENTE"
    AccountType.SAVINGS -> "POUPANÇA"
    AccountType.CREDIT -> "CARTÃO DE CRÉDITO"
}
