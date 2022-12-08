package com.topiichat.app.features.registration.domain.usecases

import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import java.util.UUID
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) : UseCase<RegisterUseCase.Params, RegisterDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<RegisterDomain> {
        return registerRepository.register(
            phoneNumber = params?.phoneNumber ?: "",
            code = params?.code ?: "",
            authyId = params?.authyId ?: "",
            pinCode = params?.pinCode ?: "",
            aliceUserId = params?.aliceUserId ?: UUID.randomUUID().toString()
        )
    }

    data class Params(
        val phoneNumber: String?,
        val code: String?,
        val authyId: String,
        val pinCode: String,
        val aliceUserId: String? = null
    )
}