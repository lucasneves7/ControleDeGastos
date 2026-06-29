package com.lucasneves.financecontrol.ui.creditcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.ui.components.TransactionListItem
import com.lucasneves.financecontrol.ui.theme.ExpenseRed
import com.lucasneves.financecontrol.util.CurrencyUtils
import com.lucasneves.financecontrol.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardScreen(
    bottomPadding: PaddingValues,
    onEditTransaction: (String) -> Unit,
    viewModel: CreditCardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(bottomPadding)) {
        TopAppBar(title = { Text("Fatura") })

        when {
            state.isLoading -> {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                }
            }
            state.creditAccounts.isEmpty() -> {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Nenhum cartão de crédito cadastrado.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.loadData(refresh = true) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    val transactions = viewModel.getBillingTransactions()
                    val grouped = transactions.groupBy { it.date }

                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
                        if (state.creditAccounts.size > 1) {
                            item {
                                CardSelector(
                                    accounts = state.creditAccounts,
                                    selectedId = state.selectedAccountId,
                                    onSelect = { viewModel.selectAccount(it) },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        val start = state.billingPeriodStart
                        val end = state.billingPeriodEnd
                        if (start != null && end != null) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(onClick = { viewModel.previousBillingPeriod() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Período anterior")
                                    }
                                    Text(
                                        text = "${start.dayOfMonth}/${start.monthNumber} – ${end.dayOfMonth}/${end.monthNumber}/${end.year}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    IconButton(onClick = { viewModel.nextBillingPeriod() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próximo período")
                                    }
                                }
                            }

                            val account = state.creditAccounts.find { it.id == state.selectedAccountId }
                            item {
                                val billingTotal = viewModel.getBillingTotal()
                                val available = ((account?.creditLimit ?: 0.0) - billingTotal).coerceAtLeast(0.0)
                                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Fatura", style = MaterialTheme.typography.labelMedium)
                                            Text(CurrencyUtils.format(billingTotal), style = MaterialTheme.typography.titleMedium, color = ExpenseRed)
                                        }
                                        HorizontalDivider()
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Limite disponível", style = MaterialTheme.typography.bodySmall)
                                            Text(CurrencyUtils.format(available))
                                        }
                                        if (account?.dueDay != null) {
                                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Vencimento", style = MaterialTheme.typography.bodySmall)
                                                Text("Dia ${account.dueDay}")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (transactions.isEmpty()) {
                            item {
                                Text(
                                    "Nenhum lançamento neste período.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
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
                                            accountName = state.creditAccounts.find { it.id == t.accountId }?.name ?: "",
                                            onClick = { onEditTransaction(t.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardSelector(
    accounts: List<Account>,
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
            label = { Text("Cartão") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            accounts.forEach { acc ->
                DropdownMenuItem(text = { Text(acc.name) }, onClick = { onSelect(acc.id); expanded = false })
            }
        }
    }
}
