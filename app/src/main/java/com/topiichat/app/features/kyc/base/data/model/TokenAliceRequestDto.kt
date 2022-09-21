package com.topiichat.app.features.kyc.base.data.model

import com.squareup.moshi.Json

data class TokenAliceRequestDto(
    @Json(name = "email")
    val email: String,
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "lastName")
    val lastName: String
)