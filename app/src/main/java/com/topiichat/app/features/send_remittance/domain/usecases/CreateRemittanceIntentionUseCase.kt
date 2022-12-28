package com.topiichat.app.features.send_remittance.domain.usecases

import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.send_remittance.data.model.SendPaymentIntentionRequestDto
import com.topiichat.app.features.send_remittance.data.model.toDto
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain
import com.topiichat.app.features.send_remittance.domain.repo.SendRemittanceRepository
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import com.topiichat.core.exception.domain.emitError
import javax.inject.Inject

class CreateRemittanceIntentionUseCase @Inject constructor(
    private val getToken: GetAuthDataUseCase,
    private val sendPaymentRepository: SendRemittanceRepository
) : UseCase<CreateRemittanceIntentionUseCase.Params, FxRateDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<FxRateDomain> {
        val tokenResult = getToken()
        params?.fxRate?.let {
            return sendPaymentRepository.createRemittanceIntention(
                tokenResult.accessToken,
                SendPaymentIntentionRequestDto(
                    it.toDto(),
                    params.recipientId,
                    tokenResult.senderId
                )
            )
        } ?: return ResultData.Fail(emitError("CreateRemittanceIntentionUseCase fxRate param is null"))
    }

    data class Params(
        val recipientId: String,
        val fxRate: FxRateDomain?
    )
}