package com.topiichat.app.features.send_remittance.data.model

import com.squareup.moshi.Json
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain

data class SendPaymentIntentionRequestDto(
    @Json(name = "buckzyData")
    val buckzyData: BuckzyData,
    @Json(name = "recipientId")
    val recipientId: String,
    @Json(name = "senderId")
    val senderId: String
) {
    data class BuckzyData(
        @Json(name = "amount")
        val amount: Double,
        @Json(name = "convertingAmount")
        val convertingAmount: Double,
        @Json(name = "exchangeRate")
        val exchangeRate: Double,
        @Json(name = "fee")
        val fee: Fee,
        @Json(name = "fromCountryCode")
        val fromCountryCode: String,
        @Json(name = "fromCurrencyCode")
        val fromCurrencyCode: String,
        @Json(name = "fxSpotId")
        val fxSpotId: String,
        @Json(name = "receivingAmount")
        val receivingAmount: Double,
        @Json(name = "sendingAmount")
        val sendingAmount: Double,
        @Json(name = "toCountryCode")
        val toCountryCode: String,
        @Json(name = "toCurrencyCode")
        val toCurrencyCode: String
    ) {
        data class Fee(
            @Json(name = "amount")
            val amount: Double,
            @Json(name = "buckzy")
            val buckzy: Buckzy,
            @Json(name = "currencyCode")
            val currencyCode: String,
            @Json(name = "disbursementAmount")
            val disbursementAmount: Double,
            @Json(name = "fundingAmount")
            val fundingAmount: Double,
            @Json(name = "partner")
            val partner: Partner,
            @Json(name = "totalAmount")
            val totalAmount: Double
        ) {
            data class Buckzy(
                @Json(name = "amount")
                val amount: Double,
                @Json(name = "disbursementAmount")
                val disbursementAmount: Double,
                @Json(name = "fundingAmount")
                val fundingAmount: Double
            )

            data class Partner(
                @Json(name = "amount")
                val amount: Double,
                @Json(name = "disbursementAmount")
                val disbursementAmount: Double,
                @Json(name = "fundingAmount")
                val fundingAmount: Double
            )
        }
    }
}

fun FxRateDomain.Fee.Buckzy.toDto(): SendPaymentIntentionRequestDto.BuckzyData.Fee.Buckzy {
    return SendPaymentIntentionRequestDto.BuckzyData.Fee.Buckzy(
        amount = amount,
        disbursementAmount = disbursementAmount,
        fundingAmount = fundingAmount
    )
}

fun FxRateDomain.Fee.Partner.toDto(): SendPaymentIntentionRequestDto.BuckzyData.Fee.Partner {
    return SendPaymentIntentionRequestDto.BuckzyData.Fee.Partner(
        amount = amount,
        disbursementAmount = disbursementAmount,
        fundingAmount = fundingAmount
    )
}

fun FxRateDomain.toDto(): SendPaymentIntentionRequestDto.BuckzyData {
    return SendPaymentIntentionRequestDto.BuckzyData(
        amount = amount,
        convertingAmount = convertingAmount,
        exchangeRate = exchangeRate,
        fee = fee.toDto(),
        fromCountryCode = fromCountryCode,
        fromCurrencyCode = fromCurrencyCode,
        fxSpotId = fxSpotId,
        receivingAmount = receivingAmount,
        sendingAmount = sendingAmount,
        toCountryCode = toCountryCode,
        toCurrencyCode = toCurrencyCode
    )
}

fun FxRateDomain.Fee.toDto(): SendPaymentIntentionRequestDto.BuckzyData.Fee {
    return SendPaymentIntentionRequestDto.BuckzyData.Fee(
        amount,
        buckzy.toDto(),
        currencyCode,
        disbursementAmount,
        fundingAmount,
        partner.toDto(),
        totalAmount
    )
}