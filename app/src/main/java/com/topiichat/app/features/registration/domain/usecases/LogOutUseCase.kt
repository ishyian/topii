package com.topiichat.app.features.registration.domain.usecases

import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import com.topiichat.core.domain.EmptyDomain
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
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