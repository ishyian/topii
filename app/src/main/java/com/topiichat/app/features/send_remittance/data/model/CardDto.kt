package com.topiichat.app.features.send_remittance.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class CardDto(
    @Json(name = "expiryMonth")
    val expiryMonth: Int?,
    @Json(name = "expiryYear")
    val expiryYear: Int?,
    @Json(name = "last4")
    val last4: String,
    @Json(name = "network")
    val network: String,
    @Json(name = "token")
    val token: String
) : Dto