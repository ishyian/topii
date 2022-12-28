package com.topiichat.app.features.request_remittance.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.features.request_remittance.data.model.RequestRemittanceDto
import com.topiichat.app.features.request_remittance.data.model.RequestRemittanceRequestDto
import com.topiichat.core.data.datasource.BaseRemoteDataStore
import com.topiichat.core.domain.ResultData
import javax.inject.Inject

class RequestRemittanceRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

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
    ): ResultData<RequestRemittanceDto> {
        val request = RequestRemittanceRequestDto(
            description = description,
            fromCountryCode = fromCountryCode,
            fromCurrencyCode = fromCurrencyCode,
            purposeCode = purposeCode,
            requestedAmount = amount.toString(),
            toCountryCode = toCountryCode,
            toCurrencyCode = toCurrencyCode,
            senderDialCountryCode = senderDialCode,
            senderMobileNumber = senderNumber
        )
        return safeApiCall {
            apiService.requestRemittance(
                accessToken = accessToken,
                requestDto = request
            )
        }
    }

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
    ): ResultData<RequestRemittanceDto> {
        val request = RequestRemittanceRequestDto(
            description = description,
            fromCountryCode = fromCountryCode,
            fromCurrencyCode = fromCurrencyCode,
            purposeCode = purposeCode,
            requestedAmount = amount.toString(),
            toCountryCode = toCountryCode,
            toCurrencyCode = toCurrencyCode,
            senderId = senderId
        )
        return safeApiCall {
            apiService.requestRemittance(
                accessToken = accessToken,
                requestDto = request
            )
        }
    }
}