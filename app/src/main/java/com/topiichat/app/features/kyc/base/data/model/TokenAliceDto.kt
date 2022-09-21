package com.topiichat.app.features.kyc.base.data.model

import com.squareup.moshi.Json

data class TokenAliceDto(
    @Json(name = "token")
    val token: String
)