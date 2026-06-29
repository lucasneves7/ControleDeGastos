package com.lucasneves.financecontrol.ui.statement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasneves.financecontrol.ui.components.MonthNavigator
import com.lucasneves.financecontrol.ui.components.TransactionListItem
import com.lucasneves.financecontrol.ui.theme.ExpenseRed
import com.lucasneves.financecontrol.ui.theme.IncomeGreen
import com.lucasneves.financecontrol.util.CurrencyUtils
import com.lucasneves.financecontrol.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementScreen(
    bottomPadding: PaddingValues,
    onEditTransaction: (String) -> Unit,
    viewModel: StatementViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val transactions = viewModel.getFilteredTransactions()
    val grouped = transactions.groupBy { it.date }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottomPadding)
    ) {
        TopAppBar(title = { Text("Extrato") })

        if (state.isLoading) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { CircularProgressIndicator() }
            return@Column
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.loadData(refresh = true) },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Account selector
                item {
                    AccountSelector(
                        accounts = state.accounts,
                        selectedId = state.selectedAccountId,
                        onSelect = { viewModel.selectAccount(it) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Month navigator
                item {
                    MonthNavigator(
                        selectedMonth = state.selectedMonth,
                        onPrevious = { viewModel.selectMonth(DateUtils.previousMonth(state.selectedMonth)) },
                        onNext = { viewModel.selectMonth(DateUtils.nextMonth(state.selectedMonth)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Summary card
                item {
                    val account = state.accounts.find { it.id == state.selectedAccountId }
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Saldo atual", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    CurrencyUtils.format(account?.balance ?: 0.0),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            HorizontalDivider()
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Entradas", style = MaterialTheme.typography.labelSmall)
                                    Text(CurrencyUtils.format(viewModel.getMonthlyIncome()), color = IncomeGreen)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Saídas", style = MaterialTheme.typography.labelSmall)
                                    Text(CurrencyUtils.format(viewModel.getMonthlyExpense()), color = ExpenseRed)
                                }
                            }
                        }
                    }
                }

                // Filter tabs
                item {
                    val filters = StatementFilter.entries.toTypedArray()
                    PrimaryTabRow(selectedTabIndex = filters.indexOf(state.filter)) {
                        filters.forEachIndexed { index, filter ->
                            Tab(
                                selected = state.filter == filter,
                                onClick = { viewModel.setFilter(filter) },
                                text = { Text(filter.label, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }

                // Transactions grouped by date
                grouped.forEach { (date, txs) ->
                    item {
                        Text(
                            text = DateUtils.toDisplayDate(date),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    txs.forEach { t ->
                        item {
                            TransactionListItem(
                                transaction = t,
                                category = state.categories.find { it.id == t.categoryId },
                                accountName = state.accounts.find { it.id == t.accountId }?.name ?: "",
                                onClick = { onEditTransaction(t.id) }
                            )
                        }
                    }
                }

                if (transactions.isEmpty()) {
                    item {
                        Text(
                            "Nenhuma transação encontrada.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountSelector(
    accounts: List<com.lucasneves.financecontrol.data.model.Account>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = accounts.find { it.id == selectedId }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Conta") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            accounts.forEach { acc ->
                DropdownMenuItem(
                    text = { Text(acc.name) },
                    onClick = { onSelect(acc.id); expanded = false }
                )
            }
        }
    }
}
