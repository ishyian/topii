package com.topiichat.app.features.valid_phone_number.domain.usecases

import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.valid_phone_number.domain.model.ValidPhone
import javax.inject.Inject

class ValidPhoneNumberUseCase @Inject constructor(

) : UseCase<ValidPhoneNumberUseCase.Params, ValidPhone>() {

    override operator fun invoke(params: Params?): ValidPhone {
        return ValidPhone.Success(params?.phoneNumber ?: "")
    }

    data class Params(
        val phoneNumber: String
    )
}