package com.topiichat.app.features.send_remittance.domain.usecases

import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import com.topiichat.app.features.send_remittance.domain.repo.SendRemittanceRepository
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import com.topiichat.core.exception.domain.emitError
import javax.inject.Inject

class SendRemittanceUseCase @Inject constructor(
    private val getToken: GetAuthDataUseCase,
    private val sendPaymentRepository: SendRemittanceRepository
) : UseCase<SendRemittanceUseCase.Params, RemittanceDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<RemittanceDomain> {
        val tokenResult = getToken()
        params?.let {
            return sendPaymentRepository.sendRemittance(
                tokenResult.accessToken,
                it.recipientId,
                it.fxRateId,
                it.description,
                it.purposeCode,
                it.cardId
            )
        } ?: return ResultData.Fail(emitError("SendRemittanceUseCase params is null"))
    }

    data class Params(
        val recipientId: String,
        val fxRateId: String,
        val description: String,
        val purposeCode: String,
        val cardId: String
    )
}