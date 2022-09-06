package com.topiichat.app.features.send_remittance.domain.model

import com.topiichat.app.core.data.Dto

data class CardDomain(
    val cardId: String,
    val expiryMonth: Int?,
    val expiryYear: Int?,
    val last4Digits: String,
    val network: String
) : Dto