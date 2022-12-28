package com.topiichat.app.features.send_remittance.domain.usecases

import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.domain.repo.SendRemittanceRepository
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import javax.inject.Inject

class GetRemittancePurposesUseCase @Inject constructor(
    private val sendPaymentRepository: SendRemittanceRepository
) : UseCase<GetRemittancePurposesUseCase.Params, List<RemittancePurposeDomain>> {

    override suspend operator fun invoke(params: Params?): ResultData<List<RemittancePurposeDomain>> {
        return sendPaymentRepository.getRemittancePurposes()
    }

    object Params
}