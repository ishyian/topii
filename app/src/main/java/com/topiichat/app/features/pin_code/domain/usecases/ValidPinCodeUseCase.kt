package com.topiichat.app.features.pin_code.domain.usecases

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.core.exception.domain.emitError
import com.topiichat.app.features.pin_code.domain.model.ValidPinCodeDomain
import com.topiichat.app.features.pin_code.domain.repo.PinCodeRepository
import javax.inject.Inject

class ValidPinCodeUseCase @Inject constructor(
    private val pinCodeRepository: PinCodeRepository
) : UseCase<ValidPinCodeUseCase.Params, ValidPinCodeDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<ValidPinCodeDomain> {
        return params?.let {
            pinCodeRepository.validatePinCode(it.pinCode)
        } ?: ResultData.Fail(emitError("ValidPinCodeUseCase params are null"))
    }

    data class Params(
        val pinCode: String
    )
}