package com.topiichat.app.features.send_remittance.domain.model

import com.topiichat.app.core.domain.Domain

data class FxRateDomain(
    val amount: Double,
    val convertingAmount: Double,
    val exchangeRate: Double,
    val fee: Fee,
    val fromCountryCode: String,
    val fromCurrencyCode: String,
    val fxSpotId: String,
    val receivingAmount: Double,
    val sendingAmount: Double,
    val toCountryCode: String,
    val toCurrencyCode: String
) : Domain {
    data class Fee(
        val amount: Double,
        val buckzy: Buckzy,
        val currencyCode: String,
        val disbursementAmount: Double,
        val fundingAmount: Double,
        val partner: Partner,
        val totalAmount: Double
    ) : Domain {
        data class Buckzy(
            val amount: Double,
            val disbursementAmount: Double,
            val fundingAmount: Double
        ) : Domain

        data class Partner(
            val amount: Double,
            val disbursementAmount: Double,
            val fundingAmount: Double
        ) : Domain
    }
}