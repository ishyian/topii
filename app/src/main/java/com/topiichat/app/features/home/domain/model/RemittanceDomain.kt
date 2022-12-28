package com.topiichat.app.features.home.domain.model

import com.topiichat.core.domain.Domain
import org.threeten.bp.LocalDateTime
import java.util.Currency

data class RemittanceDomain(
    val userName: String,
    val date: LocalDateTime,
    val amountText: String,
    val currency: Currency?,
    val action: RemittanceType,
    val avatar: String
) : Domain

enum class RemittanceType {
    SEND, REQUEST
}