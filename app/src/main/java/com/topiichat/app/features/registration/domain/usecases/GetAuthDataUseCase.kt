package com.topiichat.app.features.registration.domain.usecases

import com.topiichat.app.core.domain.BaseUseCase
import com.topiichat.app.features.registration.domain.model.AuthDataDomain
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import javax.inject.Inject

class GetAuthDataUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) : BaseUseCase<GetAuthDataUseCase.Params, AuthDataDomain>() {

    override suspend operator fun invoke(
        params: Params?
    ): AuthDataDomain {
        return registerRepository.getAuthData()
    }

    object Params
}