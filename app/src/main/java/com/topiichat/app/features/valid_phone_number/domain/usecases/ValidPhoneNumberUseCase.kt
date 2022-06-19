package com.topiichat.app.features.valid_phone_number.domain.usecases

import com.topiichat.app.core.domain.MockUseCase
import com.topiichat.app.features.valid_phone_number.domain.model.ValidPhone
import javax.inject.Inject

class ValidPhoneNumberUseCase @Inject constructor(

) : MockUseCase<ValidPhoneNumberUseCase.Params, ValidPhone>() {

    override suspend operator fun invoke(params: Params?): ValidPhone {
        return ValidPhone.Success(params?.phoneNumber ?: "")
    }

    data class Params(
        val phoneNumber: String
    )
}