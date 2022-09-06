package com.topiichat.app.features.home.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class RemittanceHistoryRequestDto(
    @Json(name = "year")
    val year: Int,
    @Json(name = "month")
    val month: Int,
    @Json(name = "senderId")
    val senderId: String,
    @Json(name = "skip")
    val skip: Int,
    @Json(name = "first")
    val first: Int
) : Dto
