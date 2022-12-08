package com.topiichat.app.features.send_remittance.domain.usecases

import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain
import com.topiichat.app.features.send_remittance.domain.repo.SendRemittanceRepository
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import com.topiichat.core.exception.domain.emitError
import javax.inject.Inject

class GetFxRateUseCase @Inject constructor(
    private val getToken: GetAuthDataUseCase,
    private val sendRemittanceRepository: SendRemittanceRepository
) : UseCase<GetFxRateUseCase.Params, FxRateDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<FxRateDomain> {
        val tokenResult = getToken()
        params?.let {
            return sendRemittanceRepository.getFxRate(
                accessToken = tokenResult.accessToken,
                fromCountryCode = tokenResult.isoCode,
                fromCurrencyCode = it.fromCurrencyCode,
                toCurrencyCode = it.toCurrencyCode,
                toCountryCode = it.toCountryCode,
                sendAmount = it.sendAmount
            )
        } ?: return ResultData.Fail(emitError("FetchRemittanceHistoryUseCase params are null"))
    }

    data class Params(
        val fromCurrencyCode: String,
        val toCurrencyCode: String,
        val toCountryCode: String,
        val sendAmount: Double
    )
}