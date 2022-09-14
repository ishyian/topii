package com.topiichat.app.features.registration.domain.usecases

import com.topiichat.app.core.domain.EmptyDomain
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) : UseCase<LogOutUseCase.Params, EmptyDomain> {

    override suspend operator fun invoke(
        params: Params?
    ): ResultData<EmptyDomain> {
        return registerRepository.deleteAuthData()
    }

    object Params
}