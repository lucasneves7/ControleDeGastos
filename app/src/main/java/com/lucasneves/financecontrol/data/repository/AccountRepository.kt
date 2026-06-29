package com.lucasneves.financecontrol.data.repository

import com.lucasneves.financecontrol.data.model.Account
import com.lucasneves.financecontrol.data.model.AccountType
import com.lucasneves.financecontrol.data.remote.SheetsApiService
import com.lucasneves.financecontrol.data.remote.dto.BatchRequestItemDto
import com.lucasneves.financecontrol.data.remote.dto.BatchUpdateRequestDto
import com.lucasneves.financecontrol.data.remote.dto.DeleteDimensionDto
import com.lucasneves.financecontrol.data.remote.dto.DimensionRangeDto
import com.lucasneves.financecontrol.data.remote.dto.ValueRangeDto
import com.lucasneves.financecontrol.util.Constants
import com.lucasneves.financecontrol.util.DateUtils
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val sheetsApiService: SheetsApiService,
    private val spreadsheetRepository: SpreadsheetRepository
) {
    // id, name, type, balance, creditLimit, billingDay, dueDay, createdAt
    private val range = "${Constants.SHEET_ACCOUNTS}!A:H"

    suspend fun getAccounts(): List<Account> {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return emptyList()
        return rows.drop(1).mapNotNull { it.toAccount() }
    }

    suspend fun addAccount(
        name: String,
        type: AccountType,
        initialBalance: Double,
        creditLimit: Double? = null,
        billingDay: Int? = null,
        dueDay: Int? = null
    ): Account {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val account = Account(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            balance = initialBalance,
            creditLimit = creditLimit,
            billingDay = billingDay,
            dueDay = dueDay,
            createdAt = DateUtils.today()
        )
        sheetsApiService.appendValues(id, range, body = ValueRangeDto(values = listOf(account.toRow())))
        return account
    }

    suspend fun updateAccount(account: Account) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return
        val rowIndex = rows.indexOfFirst { it.firstOrNull() == account.id }
        if (rowIndex == -1) return
        sheetsApiService.updateValues(
            id, "${Constants.SHEET_ACCOUNTS}!A${rowIndex + 1}:H${rowIndex + 1}",
            body = ValueRangeDto(values = listOf(account.toRow()))
        )
    }

    suspend fun deleteAccount(accountId: String) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return
        val rowIndex = rows.indexOfFirst { it.firstOrNull() == accountId }
        if (rowIndex == -1) return
        val sheetId = spreadsheetRepository.getSheetId(Constants.SHEET_ACCOUNTS)
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

    suspend fun updateBalance(accountId: String, newBalance: Double) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return
        val rowIndex = rows.indexOfFirst { it.firstOrNull() == accountId }
        if (rowIndex == -1) return
        sheetsApiService.updateValues(
            id, "${Constants.SHEET_ACCOUNTS}!D${rowIndex + 1}",
            body = ValueRangeDto(values = listOf(listOf(newBalance.toString())))
        )
    }

    private fun List<String>.toAccount(): Account? = runCatching {
        Account(
            id = getOrElse(0) { "" },
            name = getOrElse(1) { "" },
            type = AccountType.valueOf(getOrElse(2) { AccountType.CASH.name }),
            balance = getOrElse(3) { "0.0" }.toDoubleOrNull() ?: 0.0,
            creditLimit = getOrElse(4) { "" }.toDoubleOrNull(),
            billingDay = getOrElse(5) { "" }.toIntOrNull(),
            dueDay = getOrElse(6) { "" }.toIntOrNull(),
            createdAt = getOrElse(7) { "" }
        )
    }.getOrNull()

    private fun Account.toRow() = listOf(
        id, name, type.name, balance.toString(),
        creditLimit?.toString() ?: "",
        billingDay?.toString() ?: "",
        dueDay?.toString() ?: "",
        createdAt
    )
}
