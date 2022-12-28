package com.topiichat.app.features.home.data.mapper

import com.topiichat.app.features.home.data.model.RemittanceHistoryDto
import com.topiichat.app.features.home.domain.model.RemittanceDomain
import com.topiichat.app.features.home.domain.model.RemittanceHistoryDomain
import com.topiichat.app.features.home.domain.model.RemittanceType
import com.topiichat.core.domain.Mapper
import org.threeten.bp.LocalDateTime
import java.util.Currency
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
                        amountText = String.format("%.02f", converting?.amount),
                        action = RemittanceType.SEND,
                        currency = Currency.getInstance(converting?.currency),
                        avatar = recipientProfile.avatar
                    )
                } else {
                    val senderProfile = remittance.recipient.profile
                    RemittanceDomain(
                        userName = "${senderProfile.firstName} ${senderProfile.lastName}",
                        date = LocalDateTime.parse(remittance.createdAt),
                        amountText = String.format("%.02f", receiving?.amount),
                        action = RemittanceType.REQUEST,
                        currency = Currency.getInstance(receiving?.currency),
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