package com.topiichat.app.features.send_remittance.domain.repo

import com.topiichat.app.features.send_remittance.data.model.SendPaymentIntentionRequestDto
import com.topiichat.app.features.send_remittance.domain.model.CardDomain
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.core.domain.ResultData

interface SendRemittanceRepository {
    suspend fun getFxRate(
        accessToken: String,
        fromCurrencyCode: String,
        fromCountryCode: String,
        toCurrencyCode: String,
        toCountryCode: String,
        sendAmount: Double
    ): ResultData<FxRateDomain>

    suspend fun getRemittancePurposes(): ResultData<List<RemittancePurposeDomain>>

    suspend fun getCards(accessToken: String): ResultData<List<CardDomain>>

    suspend fun createRemittanceIntention(
        accessToken: String,
        requestDto: SendPaymentIntentionRequestDto
    ): ResultData<FxRateDomain>

    suspend fun sendRemittance(
        accessToken: String,
        recipientId: String,
        fxRateId: String,
        description: String,
        purposeCode: String,
        cardId: String
    ): ResultData<RemittanceDomain>
}