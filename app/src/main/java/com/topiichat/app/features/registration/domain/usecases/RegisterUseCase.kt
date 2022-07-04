package com.topiichat.app.features.registration.domain.usecases

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) : UseCase<RegisterUseCase.Params, RegisterDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<RegisterDomain> {
        return registerRepository.register(
            phoneNumber = params?.phoneNumber ?: "",
            code = params?.code ?: "",
            authyId = params?.authyId ?: "",
            pinCode = params?.pinCode ?: ""
        )
    }

    data class Params(
        val phoneNumber: String?,
        val code: String?,
        val authyId: String,
        val pinCode: String
    )
}