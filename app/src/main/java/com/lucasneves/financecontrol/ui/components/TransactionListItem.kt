package com.lucasneves.financecontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val (amountText, amountColor, dotColor) = when (transaction.type) {
        TransactionType.INCOME   -> Triple("+${CurrencyUtils.format(transaction.amount)}", IncomeGreen,   IncomeGreen)
        TransactionType.EXPENSE  -> Triple("-${CurrencyUtils.format(transaction.amount)}", ExpenseRed,    ExpenseRed)
        TransactionType.TRANSFER -> Triple(CurrencyUtils.format(transaction.amount),       TransferBlue,  TransferBlue)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Indicador colorido de tipo
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.description.ifEmpty { category?.name ?: "Transferência" },
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            Text(
                text = accountName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = amountText,
            style = MaterialTheme.typography.titleSmall,
            color = amountColor
        )
    }
}
