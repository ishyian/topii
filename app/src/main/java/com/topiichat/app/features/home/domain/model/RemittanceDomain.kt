package com.topiichat.app.features.home.domain.model

import com.topiichat.core.domain.Domain
import org.threeten.bp.LocalDateTime

data class RemittanceDomain(
    val userName: String,
    val date: LocalDateTime,
    val amountText: String,
    val action: RemittanceType,
    val avatar: String
) : Domain

enum class RemittanceType {
    SEND, REQUEST
}