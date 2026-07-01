package com.lucasneves.financecontrol.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.lucasneves.financecontrol.data.remote.DriveApiService
import com.lucasneves.financecontrol.data.remote.SheetsApiService
import com.lucasneves.financecontrol.data.remote.dto.CreateSpreadsheetDto
import com.lucasneves.financecontrol.data.remote.dto.SheetDto
import com.lucasneves.financecontrol.data.remote.dto.SheetPropertiesDto
import com.lucasneves.financecontrol.data.remote.dto.SpreadsheetPropertiesDto
import com.lucasneves.financecontrol.data.remote.dto.ValueRangeDto
import com.lucasneves.financecontrol.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpreadsheetRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sheetsApiService: SheetsApiService,
    private val driveApiService: DriveApiService,
    private val prefs: SharedPreferences
) {
    private var cachedSpreadsheetId: String? = prefs.getString(Constants.PREF_SPREADSHEET_ID, null)
    private val cachedSheetIds = mutableMapOf<String, Int>()

    private suspend fun findExistingSpreadsheet(): String? = try {
        val query = "name = '${Constants.SPREADSHEET_NAME}' and mimeType = 'application/vnd.google-apps.spreadsheet' and trashed = false"
        driveApiService.listFiles(query).files.firstOrNull()?.id
    } catch (e: Exception) {
        null
    }

    suspend fun getOrCreateSpreadsheet(): String {
        cachedSpreadsheetId?.let { return it }

        val existingId = findExistingSpreadsheet()
        if (existingId != null) {
            prefs.edit().putString(Constants.PREF_SPREADSHEET_ID, existingId).apply()
            cachedSpreadsheetId = existingId
            return existingId
        }

        val response = sheetsApiService.createSpreadsheet(
            CreateSpreadsheetDto(
                properties = SpreadsheetPropertiesDto(title = Constants.SPREADSHEET_NAME),
                sheets = listOf(
                    SheetDto(SheetPropertiesDto(title = Constants.SHEET_ACCOUNTS)),
                    SheetDto(SheetPropertiesDto(title = Constants.SHEET_CATEGORIES)),
                    SheetDto(SheetPropertiesDto(title = Constants.SHEET_TRANSACTIONS))
                )
            )
        )
        val id = response.spreadsheetId
        prefs.edit().putString(Constants.PREF_SPREADSHEET_ID, id).apply()
        cachedSpreadsheetId = id
        loadSheetIds(response.sheets)
        initializeHeaders(id)
        return id
    }

    suspend fun getSheetId(sheetName: String): Int {
        cachedSheetIds[sheetName]?.let { return it }
        val id = getOrCreateSpreadsheet()
        val response = sheetsApiService.getSpreadsheet(id)
        loadSheetIds(response.sheets)
        return cachedSheetIds[sheetName] ?: 0
    }

    private fun loadSheetIds(sheets: List<SheetDto>?) {
        sheets?.forEach { sheet ->
            sheet.properties.sheetId?.let { sheetId ->
                cachedSheetIds[sheet.properties.title] = sheetId
            }
        }
    }

    private suspend fun initializeHeaders(spreadsheetId: String) {
        sheetsApiService.appendValues(
            spreadsheetId, "${Constants.SHEET_ACCOUNTS}!A1",
            body = ValueRangeDto(values = listOf(listOf("id", "name", "type", "balance", "creditLimit", "billingDay", "dueDay", "createdAt")))
        )
        sheetsApiService.appendValues(
            spreadsheetId, "${Constants.SHEET_CATEGORIES}!A1",
            body = ValueRangeDto(values = listOf(listOf("id", "name", "parentId", "type", "isDefault")))
        )
        sheetsApiService.appendValues(
            spreadsheetId, "${Constants.SHEET_TRANSACTIONS}!A1",
            body = ValueRangeDto(values = listOf(listOf("id", "date", "description", "amount", "type", "accountId", "categoryId", "toAccountId")))
        )
    }

    fun clearSpreadsheetId() {
        prefs.edit().remove(Constants.PREF_SPREADSHEET_ID).apply()
        cachedSpreadsheetId = null
        cachedSheetIds.clear()
    }
}
