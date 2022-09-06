package com.topiichat.app.features.send_remittance.data.repo

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.send_remittance.data.datasource.SendRemittanceRemoteDataSource
import com.topiichat.app.features.send_remittance.data.mapper.CardsRemoteMapper
import com.topiichat.app.features.send_remittance.data.mapper.FxRateRemoteMapper
import com.topiichat.app.features.send_remittance.data.mapper.RemittancePurposesRemoteMapper
import com.topiichat.app.features.send_remittance.data.mapper.RemittanceRemoteMapper
import com.topiichat.app.features.send_remittance.data.model.SendPaymentIntentionRequestDto
import com.topiichat.app.features.send_remittance.data.model.SendRemittanceRequestDto
import com.topiichat.app.features.send_remittance.domain.model.CardDomain
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.domain.repo.SendRemittanceRepository
import kotlinx.coroutines.withContext

class SendRemittanceRepositoryImpl(
    private val sendRemittanceRemoteDataSource: SendRemittanceRemoteDataSource,
    private val fxRateRemoteMapper: FxRateRemoteMapper,
    private val remittancePurposesRemoteMapper: RemittancePurposesRemoteMapper,
    private val remittanceRemoteMapper: RemittanceRemoteMapper,
    private val cardsMapper: CardsRemoteMapper,
    private val appDispatchers: AppDispatchers
) : SendRemittanceRepository {
    override suspend fun getFxRate(
        accessToken: String,
        fromCurrencyCode: String,
        fromCountryCode: String,
        toCurrencyCode: String,
        toCountryCode: String,
        sendAmount: Double
    ): ResultData<FxRateDomain> {
        return withContext(appDispatchers.network) {
            sendRemittanceRemoteDataSource.getFxRate(
                accessToken = accessToken,
                fromCurrencyCode = fromCurrencyCode,
                fromCountryCode = fromCountryCode,
                toCurrencyCode = toCurrencyCode,
                toCountryCode = toCountryCode,
                sendAmount = sendAmount
            ).transformData {
                fxRateRemoteMapper.map(it)
            }
        }
    }

    override suspend fun getRemittancePurposes(): ResultData<List<RemittancePurposeDomain>> {
        return withContext(appDispatchers.network) {
            sendRemittanceRemoteDataSource.getRemittancePurposes().transformData {
                remittancePurposesRemoteMapper.map(it)
            }
        }
    }

    override suspend fun getCards(accessToken: String): ResultData<List<CardDomain>> {
        return withContext(appDispatchers.network) {
            sendRemittanceRemoteDataSource.getCards(accessToken).transformData {
                cardsMapper.map(it)
            }
        }
    }

    override suspend fun createRemittanceIntention(
        accessToken: String,
        requestDto: SendPaymentIntentionRequestDto
    ): ResultData<FxRateDomain> {
        return withContext(appDispatchers.network) {
            sendRemittanceRemoteDataSource.createRemittanceIntention(
                accessToken,
                requestDto
            ).transformData {
                fxRateRemoteMapper.map(it)
            }
        }
    }

    override suspend fun sendRemittance(
        accessToken: String,
        recipientId: String,
        fxRateId: String,
        description: String,
        purposeCode: String,
        cardId: String
    ): ResultData<RemittanceDomain> {
        val request = SendRemittanceRequestDto(
            cardId = cardId,
            description = description,
            fxRateId = fxRateId,
            purposedCode = purposeCode,
            recipientId = recipientId
        )
        return withContext(appDispatchers.network) {
            sendRemittanceRemoteDataSource.sendRemittance(accessToken, request)
                .transformData {
                    remittanceRemoteMapper.map(it)
                }
        }
    }
}