package com.topiichat.app.features.home.presentation.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.home.domain.model.RemittanceHistoryDomain
import com.topiichat.app.features.home.presentation.model.HomeRemittanceHistoryUiModel
import com.topiichat.app.features.home.presentation.model.HomeTransactionUiModel
import javax.inject.Inject

class HomeRemittanceUiMapper @Inject constructor() : Mapper<RemittanceHistoryDomain, HomeRemittanceHistoryUiModel> {
    override fun map(input: RemittanceHistoryDomain?): HomeRemittanceHistoryUiModel {
        return HomeRemittanceHistoryUiModel(
            totalSent = input?.totalSent ?: 0,
            totalReceived = input?.totalReceived ?: 0,
            totalSum = input?.totalSum ?: 0,
            remittances = input?.remittances?.map { HomeTransactionUiModel(it) } ?: emptyList(),
            currency = input?.currency ?: ""
        )
    }
}