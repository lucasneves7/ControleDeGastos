package com.lucasneves.financecontrol.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ValueRangeDto(
    @SerializedName("range") val range: String? = null,
    @SerializedName("majorDimension") val majorDimension: String? = "ROWS",
    @SerializedName("values") val values: List<List<String>>? = null
)

data class CreateSpreadsheetDto(
    @SerializedName("properties") val properties: SpreadsheetPropertiesDto,
    @SerializedName("sheets") val sheets: List<SheetDto>
)

data class SpreadsheetPropertiesDto(
    @SerializedName("title") val title: String
)

data class SheetDto(
    @SerializedName("properties") val properties: SheetPropertiesDto
)

data class SheetPropertiesDto(
    @SerializedName("title") val title: String,
    @SerializedName("sheetId") val sheetId: Int? = null
)

data class SpreadsheetResponseDto(
    @SerializedName("spreadsheetId") val spreadsheetId: String,
    @SerializedName("sheets") val sheets: List<SheetDto>? = null
)

data class BatchUpdateRequestDto(
    @SerializedName("requests") val requests: List<BatchRequestItemDto>
)

data class BatchRequestItemDto(
    @SerializedName("deleteDimension") val deleteDimension: DeleteDimensionDto? = null
)

data class DeleteDimensionDto(
    @SerializedName("range") val range: DimensionRangeDto
)

data class DimensionRangeDto(
    @SerializedName("sheetId") val sheetId: Int,
    @SerializedName("dimension") val dimension: String = "ROWS",
    @SerializedName("startIndex") val startIndex: Int,
    @SerializedName("endIndex") val endIndex: Int
)

data class AppendValuesResponseDto(
    @SerializedName("spreadsheetId") val spreadsheetId: String? = null
)

data class UpdateValuesResponseDto(
    @SerializedName("spreadsheetId") val spreadsheetId: String? = null
)

data class BatchUpdateResponseDto(
    @SerializedName("spreadsheetId") val spreadsheetId: String? = null
)
