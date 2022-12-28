package com.topiichat.app.features.registration.domain.usecases

import com.topiichat.app.features.registration.domain.model.AuthDataDomain
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import com.topiichat.core.domain.EmptyDomain
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import javax.inject.Inject

class SaveAuthDataUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) : UseCase<SaveAuthDataUseCase.Params, EmptyDomain> {

    override suspend operator fun invoke(
        params: Params?
    ): ResultData<EmptyDomain> {
        return registerRepository.saveAuthData(
            AuthDataDomain(
                token = params?.accessToken ?: "",
                senderId = params?.senderId ?: "",
                isoCode = params?.isoCode ?: ""
            )
        )
    }

    data class Params(
        val accessToken: String,
        val senderId: String,
        val isoCode: String
    )
}