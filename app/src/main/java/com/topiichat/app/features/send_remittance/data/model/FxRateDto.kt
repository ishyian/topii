package com.topiichat.app.features.send_remittance.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class FxRateDto(
    @Json(name = "amount")
    val amount: String,
    @Json(name = "convertingAmount")
    val convertingAmount: String,
    @Json(name = "exchangeRate")
    val exchangeRate: String,
    @Json(name = "fee")
    val fee: Fee,
    @Json(name = "fromCountryCode")
    val fromCountryCode: String,
    @Json(name = "fromCurrencyCode")
    val fromCurrencyCode: String,
    @Json(name = "fxSpotId")
    val fxSpotId: String,
    @Json(name = "receivingAmount")
    val receivingAmount: String,
    @Json(name = "sendingAmount")
    val sendingAmount: String,
    @Json(name = "toCountryCode")
    val toCountryCode: String,
    @Json(name = "toCurrencyCode")
    val toCurrencyCode: String
) : Dto {
    data class Fee(
        @Json(name = "amount")
        val amount: String,
        @Json(name = "buckzy")
        val buckzy: Buckzy,
        @Json(name = "currencyCode")
        val currencyCode: String,
        @Json(name = "disbursementAmount")
        val disbursementAmount: String,
        @Json(name = "fundingAmount")
        val fundingAmount: String,
        @Json(name = "partner")
        val partner: Partner,
        @Json(name = "totalAmount")
        val totalAmount: String
    ) : Dto {
        data class Buckzy(
            @Json(name = "amount")
            val amount: Double,
            @Json(name = "disbursementAmount")
            val disbursementAmount: Double,
            @Json(name = "fundingAmount")
            val fundingAmount: Double
        ) : Dto

        data class Partner(
            @Json(name = "amount")
            val amount: Double,
            @Json(name = "disbursementAmount")
            val disbursementAmount: Double,
            @Json(name = "fundingAmount")
            val fundingAmount: Double
        ) : Dto
    }
}