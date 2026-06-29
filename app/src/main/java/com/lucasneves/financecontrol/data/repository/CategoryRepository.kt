package com.lucasneves.financecontrol.data.repository

import com.lucasneves.financecontrol.data.model.Category
import com.lucasneves.financecontrol.data.model.CategoryType
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
class CategoryRepository @Inject constructor(
    private val sheetsApiService: SheetsApiService,
    private val spreadsheetRepository: SpreadsheetRepository
) {
    private val range = "${Constants.SHEET_CATEGORIES}!A:E"

    suspend fun getCategories(): List<Category> {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return emptyList()
        return rows.drop(1).mapNotNull { it.toCategory() }
    }

    suspend fun addCategory(name: String, type: CategoryType, parentId: String? = null): Category {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val category = Category(
            id = UUID.randomUUID().toString(),
            name = name,
            parentId = parentId,
            type = type,
            isDefault = false
        )
        sheetsApiService.appendValues(id, range, body = ValueRangeDto(values = listOf(category.toRow())))
        return category
    }

    suspend fun updateCategory(category: Category) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return
        val rowIndex = rows.indexOfFirst { it.firstOrNull() == category.id }
        if (rowIndex == -1) return
        sheetsApiService.updateValues(
            id, "${Constants.SHEET_CATEGORIES}!A${rowIndex + 1}:E${rowIndex + 1}",
            body = ValueRangeDto(values = listOf(category.toRow()))
        )
    }

    suspend fun deleteCategory(categoryId: String) {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        val rows = sheetsApiService.getValues(id, range).values ?: return
        val rowIndex = rows.indexOfFirst { it.firstOrNull() == categoryId }
        if (rowIndex == -1) return
        val sheetId = spreadsheetRepository.getSheetId(Constants.SHEET_CATEGORIES)
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

    suspend fun addDefaultCategories() {
        val id = spreadsheetRepository.getOrCreateSpreadsheet()
        buildDefaults().forEach { category ->
            sheetsApiService.appendValues(id, range, body = ValueRangeDto(values = listOf(category.toRow())))
        }
    }

    private fun buildDefaults(): List<Category> {
        fun expense(name: String, parentId: String? = null) =
            Category(UUID.randomUUID().toString(), name, parentId, CategoryType.EXPENSE, true)
        fun income(name: String) =
            Category(UUID.randomUUID().toString(), name, null, CategoryType.INCOME, true)

        val moradia = expense("Moradia")
        val alimentacao = expense("Alimentação")
        val transporte = expense("Transporte")

        return listOf(
            moradia,
            expense("Aluguel", moradia.id),
            expense("Energia", moradia.id),
            expense("Água", moradia.id),
            alimentacao,
            expense("Supermercado", alimentacao.id),
            expense("Restaurante", alimentacao.id),
            transporte,
            expense("Combustível", transporte.id),
            expense("Transporte Público", transporte.id),
            expense("Saúde"),
            expense("Lazer"),
            expense("Outros"),
            income("Salário"),
            income("Freelance"),
            income("Investimentos"),
            income("Outros")
        )
    }

    private fun List<String>.toCategory(): Category? = runCatching {
        Category(
            id = getOrElse(0) { "" },
            name = getOrElse(1) { "" },
            parentId = getOrElse(2) { "" }.ifEmpty { null },
            type = CategoryType.valueOf(getOrElse(3) { CategoryType.EXPENSE.name }),
            isDefault = getOrElse(4) { "false" }.toBoolean()
        )
    }.getOrNull()

    private fun Category.toRow() = listOf(id, name, parentId ?: "", type.name, isDefault.toString())
}
