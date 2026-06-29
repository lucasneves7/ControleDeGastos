package com.lucasneves.financecontrol.ui.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasneves.financecontrol.data.model.TransactionType
import com.lucasneves.financecontrol.ui.components.MonthNavigator
import com.lucasneves.financecontrol.ui.theme.ExpenseRed
import com.lucasneves.financecontrol.ui.theme.IncomeGreen
import com.lucasneves.financecontrol.util.CurrencyUtils
import com.lucasneves.financecontrol.util.DateUtils
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries

val PieColors = listOf(
    Color(0xFF1565C0), Color(0xFF2E7D32), Color(0xFFC62828), Color(0xFFE65100),
    Color(0xFF6A1B9A), Color(0xFF00695C), Color(0xFF558B2F), Color(0xFF4527A0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    bottomPadding: PaddingValues,
    onNavigateBack: () -> Unit,
    onAddTransaction: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(bottomPadding)) {
        TopAppBar(
            title = { Text("Relatórios") },
            actions = {
                IconButton(onClick = { viewModel.loadData() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
                }
            }
        )
        MonthNavigator(
                selectedMonth = state.selectedMonth,
                onPrevious = { viewModel.selectMonth(DateUtils.previousMonth(state.selectedMonth)) },
                onNext = { viewModel.selectMonth(DateUtils.nextMonth(state.selectedMonth)) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            PrimaryTabRow(selectedTabIndex = state.selectedTab.ordinal) {
                ReportTab.entries.forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.label) }
                    )
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (state.selectedTab) {
                    ReportTab.CASH_FLOW -> CashFlowTab(viewModel)
                    ReportTab.BY_CATEGORY -> ByCategoryTab(viewModel)
                    ReportTab.STATEMENT -> StatementTab(state, viewModel)
                }
            }
    }
}

@Composable
private fun CashFlowTab(viewModel: ReportsViewModel) {
    val data = viewModel.getCashFlowData()
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                series(data.map { it.second.first.toFloat() })
                series(data.map { it.second.second.toFloat() })
            }
        }
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Fluxo de Caixa — últimos 6 meses", style = MaterialTheme.typography.titleMedium)
        }
        item {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer()
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
        items(data) { (month, values) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(DateUtils.monthLabel(month), style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(CurrencyUtils.format(values.first), color = IncomeGreen)
                    Text(CurrencyUtils.format(values.second), color = ExpenseRed)
                }
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun ByCategoryTab(viewModel: ReportsViewModel) {
    val data = viewModel.getExpensesByCategory()
    val total = data.sumOf { it.second }.takeIf { it > 0 } ?: 1.0

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Gastos por Categoria", style = MaterialTheme.typography.titleMedium)
        }
        if (data.isNotEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(200.dp)) {
                        var startAngle = -90f
                        data.forEachIndexed { index, (_, value) ->
                            val sweep = (value / total * 360f).toFloat()
                            drawArc(
                                color = PieColors[index % PieColors.size],
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = true
                            )
                            startAngle += sweep
                        }
                    }
                }
            }
        }
        items(data.mapIndexed { i, it -> i to it }) { (index, pair) ->
            val (category, amount) = pair
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(Modifier.size(12.dp)) {
                        drawCircle(PieColors[index % PieColors.size])
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(category.name)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(CurrencyUtils.format(amount))
                    Text("${"%.1f".format(amount / total * 100)}%", style = MaterialTheme.typography.labelMedium)
                }
            }
            HorizontalDivider()
        }
        if (data.isEmpty()) {
            item {
                Text(
                    "Nenhuma despesa neste mês",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatementTab(state: ReportsUiState, viewModel: ReportsViewModel) {
    var accountExpanded by remember { mutableStateOf(false) }
    val selectedAccount = state.accounts.find { it.id == state.selectedAccountId }

    val accountTransactions = state.transactions
        .filter { it.accountId == state.selectedAccountId || it.toAccountId == state.selectedAccountId }
        .filter { DateUtils.isInMonth(it.date, state.selectedMonth) }
        .sortedBy { it.date }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            ExposedDropdownMenuBox(expanded = accountExpanded, onExpandedChange = { accountExpanded = it }) {
                OutlinedTextField(
                    value = selectedAccount?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Conta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(accountExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(expanded = accountExpanded, onDismissRequest = { accountExpanded = false }) {
                    state.accounts.forEach { account ->
                        DropdownMenuItem(
                            text = { Text(account.name) },
                            onClick = { viewModel.selectAccount(account.id); accountExpanded = false }
                        )
                    }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Saldo Atual", style = MaterialTheme.typography.labelMedium)
                    Text(
                        CurrencyUtils.format(selectedAccount?.balance ?: 0.0),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        items(accountTransactions) { t ->
            val isIncoming = t.type == TransactionType.INCOME || t.toAccountId == state.selectedAccountId
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t.description.ifEmpty { t.type.name }, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        DateUtils.toDisplayDate(t.date),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${if (isIncoming) "+" else "-"}${CurrencyUtils.format(t.amount)}",
                    color = if (isIncoming) IncomeGreen else ExpenseRed
                )
            }
            HorizontalDivider()
        }
        if (accountTransactions.isEmpty()) {
            item {
                Text(
                    "Sem lançamentos neste período",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}