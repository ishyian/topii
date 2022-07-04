package com.topiichat.app.features.valid_phone_number.domain.usecases

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.valid_phone_number.domain.model.VerifyPhoneDomain
import com.topiichat.app.features.valid_phone_number.domain.repo.ValidPhoneRepository
import javax.inject.Inject

class VerifyPhoneNumberUseCase @Inject constructor(
    private val validPhoneRepository: ValidPhoneRepository,
) : UseCase<VerifyPhoneNumberUseCase.Params, VerifyPhoneDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<VerifyPhoneDomain> {
        return validPhoneRepository.verifyPhone(
            phoneNumber = params?.phoneNumber ?: "",
            code = params?.code ?: "",
            isoCode = params?.isoCode ?: ""
        )
    }

    data class Params(
        val phoneNumber: String?,
        val code: String?,
        val isoCode: String?
    )
}