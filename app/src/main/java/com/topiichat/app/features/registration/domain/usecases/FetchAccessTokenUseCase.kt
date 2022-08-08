package com.topiichat.app.features.registration.domain.usecases

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.registration.domain.model.AccessTokenDomain
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import javax.inject.Inject

class FetchAccessTokenUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) : UseCase<FetchAccessTokenUseCase.Params, AccessTokenDomain> {

    override suspend operator fun invoke(
        params: Params?
    ): ResultData<AccessTokenDomain> {
        return registerRepository.fetchAccessToken()
    }

    object Params
}