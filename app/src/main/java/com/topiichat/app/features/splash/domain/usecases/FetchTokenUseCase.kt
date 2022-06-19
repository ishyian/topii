package com.topiichat.app.features.splash.domain.usecases

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.splash.domain.model.TokenDomain
import com.topiichat.app.features.splash.domain.repo.AuthRepository
import javax.inject.Inject

class FetchTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) : UseCase<FetchTokenUseCase.Params, TokenDomain> {

    override suspend operator fun invoke(
        params: Params?
    ): ResultData<TokenDomain> {
        return if (params?.isRemote == true) {
            authRepository.fetchTokenRemote()
        } else {
            authRepository.fetchTokenCache()
        }
    }

    data class Params(
        val isRemote: Boolean
    )
}