package com.lucasneves.financecontrol.ui.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasneves.financecontrol.data.model.AccountType
import com.lucasneves.financecontrol.ui.components.CalendarCard
import com.lucasneves.financecontrol.ui.components.DayTransactionsDialog
import com.lucasneves.financecontrol.ui.components.MonthNavigator
import com.lucasneves.financecontrol.ui.components.TransactionListItem
import com.lucasneves.financecontrol.ui.theme.ExpenseRed
import com.lucasneves.financecontrol.ui.theme.IncomeGreen
import com.lucasneves.financecontrol.util.CurrencyUtils
import com.lucasneves.financecontrol.util.DateUtils
import kotlinx.datetime.LocalDate

private const val MAX_ITEMS = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    bottomPadding: PaddingValues,
    onEditTransaction: (String) -> Unit,
    onAllTransactions: () -> Unit,
    onDayAddTransaction: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: OverviewViewModel = hiltViewModel()
) {
    LifecycleResumeEffect(Unit) {
        viewModel.loadOnResume()
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.loggedOut.collect { onLogout() }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sair da conta") },
            text  = { Text("Deseja encerrar a sessão?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; viewModel.logout() }) {
                    Text("Sair")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
            }
        )
    }

    val state by viewModel.uiState.collectAsState()
    val monthTransactions = viewModel.getMonthTransactions()

    val selectedDay = state.selectedDay
    if (selectedDay != null) {
        DayTransactionsDialog(
            day = selectedDay,
            transactions = viewModel.getDayTransactions(selectedDay),
            categories = state.categories,
            accounts = state.accounts,
            onDismiss = { viewModel.clearSelectedDay() },
            onEditTransaction = { id -> viewModel.clearSelectedDay(); onEditTransaction(id) },
            onAddTransaction  = { date -> viewModel.clearSelectedDay(); onDayAddTransaction(date) }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(bottomPadding)) {

        TopAppBar(
            title = { Text("FinanceControl", style = MaterialTheme.typography.titleLarge) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor    = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            actions = {
                IconButton(onClick = { showLogoutDialog = true }) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair")
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh    = { viewModel.loadData(refresh = true) },
                    modifier     = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {

                        // ── Seletor de mês ─────────────────────────────────
                        item {
                            Surface(
                                color    = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                MonthNavigator(
                                    selectedMonth = state.selectedMonth,
                                    onPrevious    = { viewModel.selectMonth(DateUtils.previousMonth(state.selectedMonth)) },
                                    onNext        = { viewModel.selectMonth(DateUtils.nextMonth(state.selectedMonth)) },
                                    modifier      = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // ── Card 1 — Resumo do mês ─────────────────────────
                        item {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "Saldo Total",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        CurrencyUtils.format(state.totalBalance),
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    HorizontalDivider()
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Receitas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(CurrencyUtils.format(state.monthlyIncome), color = IncomeGreen, style = MaterialTheme.typography.titleSmall)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Balanço", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            val balance = state.monthlyIncome - state.monthlyExpense
                                            Text(
                                                CurrencyUtils.format(balance),
                                                color = if (balance >= 0) IncomeGreen else ExpenseRed,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Despesas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(CurrencyUtils.format(state.monthlyExpense), color = ExpenseRed, style = MaterialTheme.typography.titleSmall)
                                        }
                                    }
                                }
                            }
                        }

                        // ── Card 2 — Saldo por conta ──────────────────────
                        item {
                            val nonCreditAccounts = state.accounts.filter { it.type != AccountType.CREDIT }
                            val visible = nonCreditAccounts.take(MAX_ITEMS)
                            if (visible.isNotEmpty()) {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                        Text(
                                            "Saldo por Conta",
                                            style    = MaterialTheme.typography.labelMedium,
                                            color    = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                        )
                                        visible.forEach { account ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(account.name, style = MaterialTheme.typography.bodyMedium)
                                                Text(
                                                    CurrencyUtils.format(account.balance),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = if (account.balance >= 0) MaterialTheme.colorScheme.onSurface else ExpenseRed
                                                )
                                            }
                                        }
                                        if (nonCreditAccounts.size > MAX_ITEMS) {
                                            TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                                                Text("Ver todas")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── Card 3 — Cartões de crédito ───────────────────
                        val creditAccounts = state.accounts.filter { it.type == AccountType.CREDIT }
                        if (creditAccounts.isNotEmpty()) {
                            item {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                        Text(
                                            "Cartões de Crédito",
                                            style    = MaterialTheme.typography.labelMedium,
                                            color    = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                        )
                                        creditAccounts.forEach { account ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(account.name, style = MaterialTheme.typography.bodyMedium)
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        "Fatura: ${CurrencyUtils.format(account.balance)}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = ExpenseRed
                                                    )
                                                    if (account.dueDay != null) {
                                                        Text(
                                                            "Vence: dia ${account.dueDay}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── Card 4 — Calendário ───────────────────────────
                        item {
                            CalendarCard(
                                selectedMonth = state.selectedMonth,
                                dayMarks      = state.dayMarks,
                                onDayClick    = { day -> viewModel.selectDay(day) },
                                modifier      = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        // ── Card 5 — Últimos lançamentos ──────────────────
                        item {
                            val recent = monthTransactions.sortedByDescending { it.date }.take(MAX_ITEMS)
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                    Text(
                                        "Últimos Lançamentos",
                                        style    = MaterialTheme.typography.labelMedium,
                                        color    = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                    )
                                    if (recent.isEmpty()) {
                                        Text(
                                            "Nenhum lançamento neste mês",
                                            style    = MaterialTheme.typography.bodySmall,
                                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                        )
                                    } else {
                                        recent.forEach { t ->
                                            TransactionListItem(
                                                transaction = t,
                                                category    = state.categories.find { it.id == t.categoryId },
                                                accountName = state.accounts.find { it.id == t.accountId }?.name ?: "",
                                                onClick     = { onEditTransaction(t.id) }
                                            )
                                        }
                                        if (monthTransactions.size > MAX_ITEMS) {
                                            TextButton(
                                                onClick  = onAllTransactions,
                                                modifier = Modifier.fillMaxWidth()
                                            ) { Text("Ver todos") }
                                        }
                                    }
                                }
                            }
                        }

                        // ── Card 6 — Despesas por categoria ───────────────
                        item {
                            val expByCategory = viewModel.getExpensesByCategory()
                            if (expByCategory.isNotEmpty()) {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                        Text(
                                            "Despesas por Categoria",
                                            style    = MaterialTheme.typography.labelMedium,
                                            color    = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                        )
                                        val maxVal = expByCategory.maxOfOrNull { it.second } ?: 1.0
                                        expByCategory.forEach { (category, amount) ->
                                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                                Row(
                                                    Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(category.name, style = MaterialTheme.typography.bodySmall)
                                                    Text(CurrencyUtils.format(amount), style = MaterialTheme.typography.bodySmall, color = ExpenseRed)
                                                }
                                                LinearProgressIndicator(
                                                    progress = { (amount / maxVal).toFloat() },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(top = 4.dp),
                                                    color            = ExpenseRed,
                                                    trackColor       = MaterialTheme.colorScheme.surfaceVariant
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
    }
}
