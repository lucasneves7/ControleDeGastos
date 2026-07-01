package com.lucasneves.financecontrol.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DriveFilesDto(
    @SerializedName("files") val files: List<DriveFileDto> = emptyList()
)

data class DriveFileDto(
    @SerializedName("id") val id: String
)
