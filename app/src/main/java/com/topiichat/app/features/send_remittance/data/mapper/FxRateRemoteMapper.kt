package com.topiichat.app.features.send_remittance.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.send_remittance.data.model.FxRateDto
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain
import javax.inject.Inject

class FxRateRemoteMapper @Inject constructor() : Mapper<FxRateDto, FxRateDomain> {
    override fun map(input: FxRateDto?): FxRateDomain {
        return FxRateDomain(
            amount = input?.amount?.toDouble() ?: 0.00,
            convertingAmount = input?.convertingAmount?.toDouble() ?: 0.00,
            exchangeRate = input?.exchangeRate?.toDouble() ?: 0.00,
            fee = FxRateDomain.Fee(
                amount = input?.fee?.amount?.toDouble() ?: 0.00,
                buckzy = FxRateDomain.Fee.Buckzy(
                    amount = input?.fee?.buckzy?.amount ?: 0.00,
                    disbursementAmount = input?.fee?.buckzy?.disbursementAmount ?: 0.00,
                    fundingAmount = input?.fee?.buckzy?.fundingAmount ?: 0.00
                ),
                currencyCode = input?.fee?.currencyCode ?: "",
                disbursementAmount = input?.fee?.disbursementAmount?.toDouble() ?: 0.00,
                fundingAmount = input?.fee?.fundingAmount?.toDouble() ?: 0.00,
                totalAmount = input?.fee?.totalAmount?.toDouble() ?: 0.00,
                partner = FxRateDomain.Fee.Partner(
                    amount = input?.fee?.partner?.amount ?: 0.00,
                    disbursementAmount = input?.fee?.partner?.disbursementAmount ?: 0.00,
                    fundingAmount = input?.fee?.partner?.fundingAmount ?: 0.00
                )
            ),
            fromCountryCode = input?.fromCountryCode ?: "",
            fromCurrencyCode = input?.fromCurrencyCode ?: "",
            fxSpotId = input?.fxSpotId ?: "",
            receivingAmount = input?.receivingAmount?.toDouble() ?: 0.00,
            sendingAmount = input?.sendingAmount?.toDouble() ?: 0.00,
            toCountryCode = input?.toCountryCode ?: "",
            toCurrencyCode = input?.toCurrencyCode ?: ""
        )
    }
}