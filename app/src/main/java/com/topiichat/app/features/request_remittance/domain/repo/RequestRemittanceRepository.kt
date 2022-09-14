package com.topiichat.app.features.request_remittance.domain.repo

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.request_remittance.domain.model.RequestRemittanceDomain

interface RequestRemittanceRepository {
    suspend fun requestRemittanceByPhone(
        accessToken: String,
        senderDialCode: String,
        senderNumber: String,
        purposeCode: String,
        description: String,
        fromCurrencyCode: String,
        fromCountryCode: String,
        toCurrencyCode: String,
        toCountryCode: String,
        amount: Double
    ): ResultData<RequestRemittanceDomain>

    suspend fun requestRemittanceBySenderId(
        accessToken: String,
        senderId: String,
        purposeCode: String,
        description: String,
        fromCurrencyCode: String,
        fromCountryCode: String,
        toCurrencyCode: String,
        toCountryCode: String,
        amount: Double
    ): ResultData<RequestRemittanceDomain>
}