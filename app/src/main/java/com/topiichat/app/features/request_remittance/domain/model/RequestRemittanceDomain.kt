package com.topiichat.app.features.request_remittance.domain.model

import com.topiichat.app.core.domain.Domain
import org.threeten.bp.LocalDateTime

data class RequestRemittanceDomain(
    val action: String,
    val recipientId: String,
    val senderId: String,
    val createdAt: LocalDateTime?,
    val description: String,
    val id: String,
    val purposedCode: String,
    val status: String
) : Domain