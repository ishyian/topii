package com.topiichat.app.features.send_remittance.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.core.data.datasource.BaseRemoteDataStore
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.send_remittance.data.model.CardDto
import com.topiichat.app.features.send_remittance.data.model.FxRateDto
import com.topiichat.app.features.send_remittance.data.model.RemittanceDto
import com.topiichat.app.features.send_remittance.data.model.RemittancePurposeDto
import com.topiichat.app.features.send_remittance.data.model.SendPaymentIntentionRequestDto
import com.topiichat.app.features.send_remittance.data.model.SendRemittanceRequestDto
import javax.inject.Inject

class SendRemittanceRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun getFxRate(
        accessToken: String,
        fromCurrencyCode: String,
        fromCountryCode: String,
        toCurrencyCode: String,
        toCountryCode: String,
        sendAmount: Double
    ): ResultData<FxRateDto?> {
        return safeApiCall {
            apiService.getFxRate(
                accessToken = accessToken,
                fromCurrencyCode = fromCurrencyCode,
                fromCountryCode = fromCountryCode,
                toCurrencyCode = toCurrencyCode,
                toCountryCode = toCountryCode,
                sendAmount = sendAmount
            )
        }
    }

    suspend fun getRemittancePurposes(): ResultData<List<RemittancePurposeDto>?> {
        return safeApiCall {
            apiService.getRemittancePurposes()
        }
    }

    suspend fun getCards(accessToken: String): ResultData<List<CardDto>?> {
        return safeApiCall {
            apiService.getCards(accessToken)
        }
    }

    suspend fun createRemittanceIntention(
        accessToken: String,
        request: SendPaymentIntentionRequestDto
    ): ResultData<FxRateDto> {
        return safeApiCall {
            apiService.createRemittanceIntention(accessToken, request)
        }
    }

    suspend fun sendRemittance(
        accessToken: String,
        request: SendRemittanceRequestDto
    ): ResultData<RemittanceDto> {
        return safeApiCall {
            apiService.sendRemittance(accessToken, request)
        }
    }
}