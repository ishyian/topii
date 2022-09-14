package com.topiichat.app.features.request_remittance.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class RequestRemittanceDto(
    @Json(name = "action")
    val action: String,
    @Json(name = "chapiiIdRecipient")
    val chapiiIdRecipient: String?,
    @Json(name = "chapiiIdSender")
    val chapiiIdSender: String?,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "purposedCode")
    val purposedCode: String,
    @Json(name = "purposedCodeLabel")
    val purposedCodeLabel: String?,
    @Json(name = "status")
    val status: String
) : Dto