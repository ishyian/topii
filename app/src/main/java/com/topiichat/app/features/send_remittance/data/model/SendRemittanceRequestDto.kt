package com.topiichat.app.features.send_remittance.data.model

import com.squareup.moshi.Json

data class SendRemittanceRequestDto(
    @Json(name = "cardTokenized")
    val cardTokenized: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "fxRateId")
    val fxRateId: String,
    @Json(name = "purposedCode")
    val purposedCode: String,
    @Json(name = "recipientId")
    val recipientId: String
)