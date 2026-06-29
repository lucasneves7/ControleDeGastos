package com.lucasneves.financecontrol.data.repository

import com.lucasneves.financecontrol.data.model.Transaction
import com.lucasneves.financecontrol.data.model.TransactionType
import com.lucasneves.financecontrol.data.remote.SheetsApiService
import com.lucasneves.financecontrol.data.remote.dto.BatchRequestItemDto
import com.lucasneves.financecontrol.data.remote.dto.BatchUpdateRequestDto
import com.lucasneves.financecontrol.data.remote.dto.DeleteDimensionDto
import com.lucasneves.financecontrol.data.remote.dto.DimensionRangeDto
import com.lucasneves.financecontrol.data.remote.dto.ValueRangeDto
import com.lucasneves.financecontrol.util.Constants
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val sheetsApiService: SheetsApiService,
    private val spreadsheetRepository: SpreadsheetRepository
) {
    private val range = "${Constants.SHEET_TRANSACTIONS}!A:H"

    suspend fun getTransactions(): List<Transaction> {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return emptyList()
        return rows.drop(1).mapNotNull { it.toTransaction() }
    }

    suspend fun addTransaction(transaction: Transaction) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        sheetsApiService.appendValues(id, range, body = ValueRangeDto(values = listOf(transaction.toRow())))
    }

    suspend fun updateTransaction(transaction: Transaction) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return
        val rowIndex = rows.indexOfFirst { it.firstOrNull() == transaction.id }
        if (rowIndex == -1) return
        sheetsApiService.updateValues(
            id, "${Constants.SHEET_TRANSACTIONS}!A${rowIndex + 1}:H${rowIndex + 1}",
            body = ValueRangeDto(values = listOf(transaction.toRow()))
        )
    }

    suspend fun deleteTransaction(transactionId: String) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return
        val rowIndex = rows.indexOfFirst { it.firstOrNull() == transactionId }
        if (rowIndex == -1) return
        val sheetId = spreadsheetRepository.getSheetId(Constants.SHEET_TRANSACTIONS)
        sheetsApiService.batchUpdate(
            id,
            BatchUpdateRequestDto(
                requests = listOf(
                    BatchRequestItemDto(
                        deleteDimension = DeleteDimensionDto(
                            range = DimensionRangeDto(sheetId = sheetId, startIndex = rowIndex, endIndex = rowIndex + 1)
                        )
                    )
                )
            )
        )
    }

    suspend fun reassignCategory(oldCategoryId: String, newCategoryId: String) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return
        rows.forEachIndexed { index, row ->
            if (index == 0) return@forEachIndexed
            if (row.getOrElse(6) { "" } == oldCategoryId) {
                val updated = row.toMutableList().apply {
                    while (size < 8) add("")
                    set(6, newCategoryId)
                }
                sheetsApiService.updateValues(
                    id, "${Constants.SHEET_TRANSACTIONS}!A${index + 1}:H${index + 1}",
                    body = ValueRangeDto(values = listOf(updated))
                )
            }
        }
    }

    suspend fun reassignAccount(oldAccountId: String, newAccountId: String) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return
        rows.forEachIndexed { index, row ->
            if (index == 0) return@forEachIndexed
            val accountId = row.getOrElse(5) { "" }
            val toAccountId = row.getOrElse(7) { "" }
            if (accountId == oldAccountId || toAccountId == oldAccountId) {
                val updated = row.toMutableList().apply {
                    while (size < 8) add("")
                    if (accountId == oldAccountId) set(5, newAccountId)
                    if (toAccountId == oldAccountId) set(7, newAccountId)
                }
                sheetsApiService.updateValues(
                    id, "${Constants.SHEET_TRANSACTIONS}!A${index + 1}:H${index + 1}",
                    body = ValueRangeDto(values = listOf(updated))
                )
            }
        }
    }

    fun createNew(
        date: String,
        description: String,
        amount: Double,
        type: TransactionType,
        accountId: String,
        categoryId: String?,
        toAccountId: String?
    ) = Transaction(
        id = UUID.randomUUID().toString(),
        date = date,
        description = description,
        amount = amount,
        type = type,
        accountId = accountId,
        categoryId = categoryId,
        toAccountId = toAccountId
    )

    private fun List<String>.toTransaction(): Transaction? = runCatching {
        Transaction(
            id = getOrElse(0) { "" },
            date = getOrElse(1) { "" },
            description = getOrElse(2) { "" },
            amount = getOrElse(3) { "0.0" }.toDoubleOrNull() ?: 0.0,
            type = TransactionType.valueOf(getOrElse(4) { TransactionType.EXPENSE.name }),
            accountId = getOrElse(5) { "" },
            categoryId = getOrElse(6) { "" }.ifEmpty { null },
            toAccountId = getOrElse(7) { "" }.ifEmpty { null }
        )
    }.getOrNull()

    private fun Transaction.toRow() = listOf(
        id, date, description, amount.toString(), type.name,
        accountId, categoryId ?: "", toAccountId ?: ""
    )
}
