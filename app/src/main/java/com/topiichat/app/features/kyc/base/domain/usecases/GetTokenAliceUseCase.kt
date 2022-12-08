package com.topiichat.app.features.kyc.base.domain.usecases

import com.topiichat.app.features.kyc.base.domain.model.TokenAliceDomain
import com.topiichat.app.features.kyc.base.domain.repo.KYCRepository
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import com.topiichat.core.exception.domain.emitError
import javax.inject.Inject

class GetTokenAliceUseCase @Inject constructor(
    private val kycRepository: KYCRepository
) : UseCase<GetTokenAliceUseCase.Params, TokenAliceDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<TokenAliceDomain> {
        return params?.let {
            kycRepository.getTokenAlice(
                email = it.email,
                firstName = it.firstName,
                lastName = it.lastName
            )
        } ?: ResultData.Fail(emitError("GetTokenAliceUseCase params are null"))
    }

    data class Params(
        val email: String,
        val firstName: String,
        val lastName: String
    )
}