package com.topiichat.app.features.home.domain.model

import com.topiichat.core.domain.Domain

data class RemittanceHistoryDomain(
    val totalSum: Long,
    val remittances: List<RemittanceDomain>,
    val totalReceived: Long,
    val totalSent: Long,
    val currency: String
) : Domain