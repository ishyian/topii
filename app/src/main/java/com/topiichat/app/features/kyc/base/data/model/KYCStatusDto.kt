package com.topiichat.app.features.kyc.base.data.model

import com.squareup.moshi.Json

data class KYCStatusDto(
    @Json(name = "status")
    val status: String
)