package com.lucasneves.financecontrol.data.remote

import com.lucasneves.financecontrol.data.remote.dto.DriveFilesDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DriveApiService {
    @GET("files")
    suspend fun listFiles(
        @Query("q") query: String,
        @Query("fields") fields: String = "files(id)"
    ): DriveFilesDto
}
