package com.topiichat.app.features.remittance.domain.usecases

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.core.exception.domain.emitError
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.remittance.domain.repo.RemittanceRepository
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import javax.inject.Inject

class GetRemittanceDetailUseCase @Inject constructor(
    private val getToken: GetAuthDataUseCase,
    private val remittanceRepository: RemittanceRepository
) : UseCase<GetRemittanceDetailUseCase.Params, RemittanceDomain> {

    override suspend operator fun invoke(
        params: Params?
    ): ResultData<RemittanceDomain> {
        val authData = getToken()
        return params?.let {
            remittanceRepository.getRemittanceDetail(
                accessToken = authData.accessToken,
                remittanceId = it.remittanceId
            )
        } ?: ResultData.Fail(emitError("GetRemittanceDetailUseCase params are null"))
    }

    data class Params(
        val remittanceId: String
    )
}