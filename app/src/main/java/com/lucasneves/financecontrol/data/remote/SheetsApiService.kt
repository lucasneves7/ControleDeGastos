package com.lucasneves.financecontrol.data.remote

import com.lucasneves.financecontrol.data.remote.dto.AppendValuesResponseDto
import com.lucasneves.financecontrol.data.remote.dto.BatchUpdateRequestDto
import com.lucasneves.financecontrol.data.remote.dto.BatchUpdateResponseDto
import com.lucasneves.financecontrol.data.remote.dto.CreateSpreadsheetDto
import com.lucasneves.financecontrol.data.remote.dto.SpreadsheetResponseDto
import com.lucasneves.financecontrol.data.remote.dto.UpdateValuesResponseDto
import com.lucasneves.financecontrol.data.remote.dto.ValueRangeDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SheetsApiService {

    @GET("spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun getValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String
    ): ValueRangeDto

    @POST("spreadsheets/{spreadsheetId}/values/{range}:append")
    suspend fun appendValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Query("valueInputOption") valueInputOption: String = "RAW",
        @Body body: ValueRangeDto
    ): AppendValuesResponseDto

    @PUT("spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun updateValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Query("valueInputOption") valueInputOption: String = "RAW",
        @Body body: ValueRangeDto
    ): UpdateValuesResponseDto

    @POST("spreadsheets/{spreadsheetId}:batchUpdate")
    suspend fun batchUpdate(
        @Path("spreadsheetId") spreadsheetId: String,
        @Body body: BatchUpdateRequestDto
    ): BatchUpdateResponseDto

    @POST("spreadsheets")
    suspend fun createSpreadsheet(@Body body: CreateSpreadsheetDto): SpreadsheetResponseDto

    @GET("spreadsheets/{spreadsheetId}")
    suspend fun getSpreadsheet(@Path("spreadsheetId") spreadsheetId: String): SpreadsheetResponseDto
}
