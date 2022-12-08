package com.topiichat.app.features.send_remittance.data.mapper

import com.topiichat.app.features.send_remittance.data.model.RemittanceDto
import com.topiichat.app.features.send_remittance.domain.model.ConvertingDomain
import com.topiichat.app.features.send_remittance.domain.model.FeeDomain
import com.topiichat.app.features.send_remittance.domain.model.ReceivingDomain
import com.topiichat.app.features.send_remittance.domain.model.RecipientDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import com.topiichat.app.features.send_remittance.domain.model.SenderDomain
import com.topiichat.app.features.send_remittance.domain.model.SendingDomain
import com.topiichat.app.features.send_remittance.domain.model.toDomain
import com.topiichat.core.domain.BaseMapper
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class RemittanceRemoteMapper @Inject constructor() : BaseMapper<RemittanceDto, RemittanceDomain> {
    override fun map(input: RemittanceDto): RemittanceDomain {
        return RemittanceDomain(
            action = input.action,
            recipientId = input.chapiiIdRecipient,
            senderId = input.chapiiIdSender,
            id = input.id,
            transactionId = input.transactionId,
            createdAt = LocalDateTime.parse(input.createdAt) ?: LocalDateTime.now(),
            description = input.description,
            exchangeRate = input.exchangeRate.toDouble(),
            fee = FeeDomain(
                amount = input.fee.amount.toDouble(),
                currency = input.fee.currency
            ),
            sending = SendingDomain(
                amount = input.sending.amount.toDouble(),
                currency = input.sending.currency
            ),
            receiving = ReceivingDomain(
                amount = input.receiving.amount.toDouble(),
                currency = input.receiving.currency
            ),
            recipient = RecipientDomain(
                id = input.recipient.id,
                isActive = input.recipient.isActive,
                profile = input.recipient.profile.toDomain()
            ),
            sender = SenderDomain(
                id = input.sender.id,
                isActive = input.sender.isActive,
                profile = input.sender.profile.toDomain()
            ),
            converting = ConvertingDomain(
                amount = input.converting.amount.toDouble(),
                currency = input.converting.currency
            ),
            status = input.status
        )
    }
}