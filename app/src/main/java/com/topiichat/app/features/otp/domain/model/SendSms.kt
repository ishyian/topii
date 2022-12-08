package com.topiichat.app.features.otp.domain.model

import com.topiichat.core.exception.domain.ErrorDomain

sealed class SendSms {
    object Success : SendSms()
    class Fail(val errorDomain: ErrorDomain) : SendSms()
}