package com.topiichat.app.features.request_remittance.data.mapper

import com.topiichat.app.features.request_remittance.data.model.RequestRemittanceDto
import com.topiichat.app.features.request_remittance.domain.model.RequestRemittanceDomain
import com.topiichat.core.domain.BaseMapper
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class RequestRemittanceRemoteMapper
@Inject constructor() : BaseMapper<RequestRemittanceDto, RequestRemittanceDomain> {
    override fun map(input: RequestRemittanceDto) = with(input) {
        RequestRemittanceDomain(
            action = action,
            recipientId = chapiiIdRecipient ?: "",
            senderId = chapiiIdSender ?: "",
            createdAt = LocalDateTime.parse(createdAt),
            description = description,
            id = id,
            purposedCode = purposedCode,
            status = status
        )
    }
}