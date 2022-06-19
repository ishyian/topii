package com.topiichat.app.features.otp.domain.usecases

import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.otp.domain.model.ValidOtp
import javax.inject.Inject

class ValidOtpUseCase @Inject constructor() : UseCase<ValidOtpUseCase.Params, ValidOtp>() {

    override operator fun invoke(params: Params?): ValidOtp {
        return if (params?.pinCode == "1111") {
            ValidOtp.Success
        } else {
            ValidOtp.Wrong
        }
    }

    data class Params(
        val pinCode: String
    )
}