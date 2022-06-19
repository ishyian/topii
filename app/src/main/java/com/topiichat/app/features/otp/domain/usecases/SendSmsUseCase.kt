package com.topiichat.app.features.otp.domain.usecases

import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.otp.domain.model.SendSms
import javax.inject.Inject

class SendSmsUseCase @Inject constructor() : UseCase<SendSmsUseCase.Params, SendSms>() {

    override operator fun invoke(params: Params?): SendSms {
        return SendSms.Success
    }

    data class Params(
        val phoneNumber: String
    )
}