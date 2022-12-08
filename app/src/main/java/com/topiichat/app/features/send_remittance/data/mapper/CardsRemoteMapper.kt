package com.topiichat.app.features.send_remittance.data.mapper

import com.topiichat.app.features.send_remittance.data.model.CardDto
import com.topiichat.app.features.send_remittance.domain.model.CardDomain
import com.topiichat.core.domain.Mapper
import javax.inject.Inject

class CardsRemoteMapper @Inject constructor() : Mapper<List<CardDto>, List<CardDomain>> {
    override fun map(input: List<CardDto>?): List<CardDomain> {
        return input?.map { card ->
            CardDomain(
                cardId = card.cardId,
                expiryMonth = card.expiryMonth,
                expiryYear = card.expiryYear,
                last4Digits = card.last4,
                network = card.network
            )
        } ?: emptyList()
    }
}