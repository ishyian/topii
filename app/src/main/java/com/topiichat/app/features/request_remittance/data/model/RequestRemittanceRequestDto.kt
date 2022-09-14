package com.topiichat.app.features.request_remittance.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class RequestRemittanceRequestDto(
    @Json(name = "description")
    val description: String,
    @Json(name = "fromCountryCode")
    val fromCountryCode: String,
    @Json(name = "fromCurrencyCode")
    val fromCurrencyCode: String,
    @Json(name = "purposeCode")
    val purposeCode: String,
    @Json(name = "requestedAmount")
    val requestedAmount: String,
    @Json(name = "toCountryCode")
    val toCountryCode: String,
    @Json(name = "toCurrencyCode")
    val toCurrencyCode: String,
    @Json(name = "senderDialCountryCode")
    val senderDialCountryCode: String? = null,
    @Json(name = "senderMobileNumber")
    val senderMobileNumber: String? = null,
    @Json(name = "senderId")
    val senderId: String? = null,
) : Dto