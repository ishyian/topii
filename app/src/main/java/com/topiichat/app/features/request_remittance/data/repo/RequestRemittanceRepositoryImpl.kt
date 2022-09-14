package com.topiichat.app.features.request_remittance.data.repo

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.features.request_remittance.data.datasource.RequestRemittanceRemoteDataSource
import com.topiichat.app.features.request_remittance.data.mapper.RequestRemittanceRemoteMapper
import com.topiichat.app.features.request_remittance.domain.repo.RequestRemittanceRepository
import kotlinx.coroutines.withContext

class RequestRemittanceRepositoryImpl(
    private val requestRemittanceDataSource: RequestRemittanceRemoteDataSource,
    private val requestRemittanceMapper: RequestRemittanceRemoteMapper,
    private val appDispatchers: AppDispatchers
) : RequestRemittanceRepository {
    override suspend fun requestRemittanceByPhone(
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
    ) = withContext(appDispatchers.network) {
        requestRemittanceDataSource.requestRemittanceByPhone(
            accessToken = accessToken,
            senderDialCode = senderDialCode,
            senderNumber = senderNumber,
            purposeCode = purposeCode,
            description = description,
            fromCurrencyCode = fromCurrencyCode,
            fromCountryCode = fromCountryCode,
            toCurrencyCode = toCurrencyCode,
            toCountryCode = toCountryCode,
            amount = amount
        ).transformData {
            requestRemittanceMapper.map(it)
        }
    }

    override suspend fun requestRemittanceBySenderId(
        accessToken: String,
        senderId: String,
        purposeCode: String,
        description: String,
        fromCurrencyCode: String,
        fromCountryCode: String,
        toCurrencyCode: String,
        toCountryCode: String,
        amount: Double
    ) = withContext(appDispatchers.network) {
        requestRemittanceDataSource.requestRemittanceBySenderId(
            accessToken = accessToken,
            senderId = senderId,
            purposeCode = purposeCode,
            description = description,
            fromCurrencyCode = fromCurrencyCode,
            fromCountryCode = fromCountryCode,
            toCurrencyCode = toCurrencyCode,
            toCountryCode = toCountryCode,
            amount = amount
        ).transformData {
            requestRemittanceMapper.map(it)
        }
    }
}