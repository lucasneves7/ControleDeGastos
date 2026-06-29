package com.lucasneves.financecontrol.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.Transaction
import com.lucasneves.financecontrol.data.model.TransactionType
import com.lucasneves.financecontrol.ui.theme.ExpenseRed
import com.lucasneves.financecontrol.ui.theme.IncomeGreen
import com.lucasneves.financecontrol.ui.theme.TransferBlue
import com.lucasneves.financecontrol.util.CurrencyUtils

@Composable
fun TransactionListItem(
    transaction: Transaction,
    category: Category?,
    accountName: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.description.ifEmpty { category?.name ?: "Transferência" },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = accountName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        val (amountText, amountColor) = when (transaction.type) {
            TransactionType.INCOME -> "+${CurrencyUtils.format(transaction.amount)}" to IncomeGreen
            TransactionType.EXPENSE -> "-${CurrencyUtils.format(transaction.amount)}" to ExpenseRed
            TransactionType.TRANSFER -> CurrencyUtils.format(transaction.amount) to TransferBlue
        }
        Text(
            text = amountText,
            style = MaterialTheme.typography.bodyLarge,
            color = amountColor
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}
