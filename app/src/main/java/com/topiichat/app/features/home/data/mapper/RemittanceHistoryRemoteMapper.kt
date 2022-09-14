package com.topiichat.app.features.home.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.home.data.model.RemittanceHistoryDto
import com.topiichat.app.features.home.domain.model.RemittanceDomain
import com.topiichat.app.features.home.domain.model.RemittanceHistoryDomain
import com.topiichat.app.features.home.domain.model.RemittanceType
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class RemittanceHistoryRemoteMapper @Inject constructor() :
    Mapper<RemittanceHistoryDto, RemittanceHistoryDomain> {
    override fun map(input: RemittanceHistoryDto?): RemittanceHistoryDomain {
        return RemittanceHistoryDomain(
            totalSum = input?.total ?: 0L,
            totalReceived = input?.totalReceived ?: 0L,
            totalSent = input?.totalSent ?: 0,
            currency = input?.currency ?: "",
            remittances = input?.remittances?.map { remittance ->
                val converting = remittance.converting
                val receiving = remittance.receiving
                if (remittance.action == SEND) {
                    val recipientProfile = remittance.recipient.profile
                    RemittanceDomain(
                        userName = "${recipientProfile.firstName} ${recipientProfile.lastName}",
                        date = LocalDateTime.parse(remittance.createdAt),
                        amountText = "- ${converting?.currency}${converting?.amount}",
                        action = RemittanceType.SEND,
                        avatar = recipientProfile.avatar
                    )
                } else {
                    val senderProfile = remittance.recipient.profile
                    RemittanceDomain(
                        userName = "${senderProfile.firstName} ${senderProfile.lastName}",
                        date = LocalDateTime.parse(remittance.createdAt),
                        amountText = "+ ${receiving?.currency}${receiving?.amount}",
                        action = RemittanceType.REQUEST,
                        avatar = senderProfile.avatar
                    )
                }
            } ?: emptyList()
        )
    }

    companion object {
        const val SEND = "send"
    }
}