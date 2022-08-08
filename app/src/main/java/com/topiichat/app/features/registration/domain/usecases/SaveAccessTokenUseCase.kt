package com.topiichat.app.features.registration.domain.usecases

import com.topiichat.app.core.domain.EmptyDomain
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.registration.domain.model.AccessTokenDomain
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import javax.inject.Inject

class SaveAccessTokenUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) : UseCase<SaveAccessTokenUseCase.Params, EmptyDomain> {

    override suspend operator fun invoke(
        params: Params?
    ): ResultData<EmptyDomain> {
        return registerRepository.saveAccessToken(
            AccessTokenDomain(
                token = params?.accessToken ?: ""
            )
        )
    }

    data class Params(
        val accessToken: String
    )
}