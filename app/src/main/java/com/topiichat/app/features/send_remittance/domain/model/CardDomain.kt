package com.topiichat.app.features.send_remittance.domain.model

import com.topiichat.core.data.Dto

data class CardDomain(
    val expiryMonth: Int?,
    val expiryYear: Int?,
    val last4Digits: String,
    val network: String,
    val token: String
) : Dto