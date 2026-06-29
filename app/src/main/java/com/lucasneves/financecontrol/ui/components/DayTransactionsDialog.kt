package com.lucasneves.financecontrol.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.Transaction
import com.lucasneves.financecontrol.util.DateUtils
import kotlinx.datetime.LocalDate

@Composable
fun DayTransactionsDialog(
    day: LocalDate,
    transactions: List<Transaction>,
    categories: List<Category>,
    accounts: List<Account>,
    onDismiss: () -> Unit,
    onEditTransaction: (String) -> Unit,
    onAddTransaction: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(DateUtils.toDisplayDate(day.toString())) },
        text = {
            Column {
                if (transactions.isEmpty()) {
                    Text(
                        "Nenhum lançamento neste dia.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(transactions) { t ->
                            TransactionListItem(
                                transaction = t,
                                category = categories.find { it.id == t.categoryId },
                                accountName = accounts.find { it.id == t.accountId }?.name ?: "",
                                onClick = { onEditTransaction(t.id) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onAddTransaction(day.toString()) }) {
                Text("Adicionar lançamento")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        }
    )
}
